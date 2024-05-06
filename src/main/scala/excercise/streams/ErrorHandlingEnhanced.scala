package excercise.streams

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl._
import org.slf4j.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object ErrorHandlingEnhanced extends App {
  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "sys")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val logger: Logger = system.log

  var problematic = ListBuffer.empty[Int]

  val eventualDone: Future[Done] = Source(-3 to 3)
    .via(Flow[Int].map(i => Try(100 / i) match {
      case Success(result) => Right(result)
      case Failure(_) => Left(i)
    }))
    .runForeach {
      case Left(i) =>
        logger.info(s"Problematic element is $i")
        problematic.addOne(i) // seems not right to me using var for such a purpose
      case Right(i) => logger.info(s"Successful computation is $i")
    }

  eventualDone.andThen {
    case Success(_) =>
      logger.info("Stream processing successfully completed.")
      problematic.size match {
        case 0 => logger.info(s"There aren't problematic elements")
        case 1 => logger.info(s"Problematic element is ${problematic.mkString}")
        case _ => logger.info(s"Problematic elements are ${problematic.mkString(", ")}")
      }
    case Failure(err) =>
      logger.info(s"Stream processing failed with: $err")
  }.andThen {
    case _ => system.terminate()
  }
}
