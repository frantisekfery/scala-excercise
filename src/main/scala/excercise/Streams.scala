package excercise

import akka.stream._
import akka.stream.scaladsl._
import akka.actor._
import akka.persistence._
import com.amazonaws.services.kinesis.model.Record
import org.slf4j.{Logger, LoggerFactory}

import java.nio.ByteBuffer
import scala.concurrent.ExecutionContextExecutor

object Streams extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  var counter = 0

  private case class DomainEvent(data: String)

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

  private val source = Source(getRecords)
  private val persistentActor = system.actorOf(Props[PersistenceActor])

  private val commitRecord = Flow[Record].map { record =>
    counter = counter + 1
    val domainEvent = DomainEvent(new String(record.getData.array()))
    logger.info(s"Commit record: $domainEvent")
    persistentActor ! domainEvent
  }

  private val graph = source
    .via(commitRecord)
    .withAttributes(ActorAttributes.supervisionStrategy(_ => Supervision.Resume))
    .runWith(Sink.ignore)


  graph.onComplete {
    logger.info(s"Counter is: $counter")
    _ => system.terminate()
  }
}
