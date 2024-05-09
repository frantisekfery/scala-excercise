package excercise.streams

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl._
import akka.stream.{ActorAttributes, Supervision}
import akka.{Done, NotUsed}
import org.slf4j.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}

object RecoverWithDataLost extends App {
  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "sys")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val logger: Logger = system.log

  val decider: Supervision.Decider = {
    case _: ArithmeticException =>
      logger.info("Caught arithmetic exception and recover, but lost problematic element.")
      Supervision.Resume
    case _ => Supervision.Stop
  }

  val source: Source[Int, NotUsed] = Source(-3 to 3).map { i =>
    logger.info(s"Processing: $i")
    i
  }

  private val eventualDone: Future[Done] = source
    .via(Flow[Int].map(100 / _).withAttributes(ActorAttributes.supervisionStrategy(decider)))
    .recover { // with this recover I am able to continue, but I drop problematic element
      case e: ArithmeticException =>
        logger.info(s"Recovered from: $e")
        111111
    }
    .runWith(Sink.foreach(i => logger.info(s"Final Value: $i")))

  eventualDone.onComplete { result =>
    logger.info(s"Stream completed with result: $result")
    system.terminate()
  }
}
