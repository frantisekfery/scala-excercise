package excercise.streams

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl._
import org.slf4j.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object ErrorHandlingWithOneSink extends App {
  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "sys")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val logger: Logger = system.log

  val resultFuture: Future[(Seq[Int], Seq[Int])] = Source(-3 to 3)
    .mapAsync(parallelism = 2) { i =>
      Future {
        Try(100 / i) match {
          case Success(result) => (Vector.empty[Int], Vector(result))
          case Failure(_) => (Vector(i), Vector.empty[Int])
        }
      }
    }
    .runWith(Sink.seq)
    .map(seq => (seq.flatMap(_._1), seq.flatMap(_._2)))

  val failures: Future[Seq[Int]] = resultFuture.map(_._1)
  val successes: Future[Seq[Int]] = resultFuture.map(_._2)

  for {
    failureNums <- failures
    successNums <- successes
  } yield {
    failureNums.size match {
      case 0 => logger.info(s"There aren't problematic elements")
      case 1 => logger.info(s"Problematic element is ${failureNums.mkString}")
      case _ => logger.info(s"Problematic elements are ${failureNums.mkString(", ")}")
    }
    successNums.size match {
      case 0 => logger.info(s"There aren't successful computation")
      case 1 => logger.info(s"Successful computation is ${successNums.mkString}")
      case _ => logger.info(s"Successful computations are ${successNums.mkString(", ")}")
    }
  }

  Future.sequence(List(failures, successes)).onComplete(_ => system.terminate())
}
