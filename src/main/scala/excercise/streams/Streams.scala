package excercise.streams

import akka.actor._
import akka.persistence._
import akka.stream.scaladsl._
import com.amazonaws.services.kinesis.model.Record
import org.slf4j.{Logger, LoggerFactory}

import java.nio.ByteBuffer
import scala.concurrent.ExecutionContextExecutor

object Streams extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  var counter = 0

  private case class DomainEvent(data: String)

  // database is not  configured here, so there will be dead letters
  private class PersistenceActor extends PersistentActor {
    override def persistenceId: String = "sample-id-1"

    def receiveCommand: Receive = {
      case event@DomainEvent(_) => persist(event)(_ => logger.info(s"Persisted: $event"))
    }

    def receiveRecover: Receive = {
      case event@DomainEvent(_) => logger.info(s"Recovered: $event")
    }
  }

  // Mocked AWS Kinesis data
  private def getRecords: List[Record] =
    for {
      firstName <- List("James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Charles", "Thomas")
      surname <- List("Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson")
      userName = firstName + " " + surname
    } yield new Record().withData(ByteBuffer.wrap(userName.getBytes()))

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val persistentActor = system.actorOf(Props[PersistenceActor])

  Source(getRecords)
    .via(Flow[Record].map { record =>
      counter = counter + 1
      val domainEvent = DomainEvent(new String(record.getData.array()))
      logger.info(s"Commit record: $domainEvent")
      // I can use here also ask '?' to wait for response If I need to handle correct order
      persistentActor ! domainEvent
    })
    .runWith(Sink.ignore).onComplete {
      logger.info(s"Counter is: $counter")
      _ => system.terminate()
    }

  Thread.sleep(5000)
}
