package excercise.future

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object FuturesTransformAndTransformWith extends App {

  val eventualInt: Future[Int] = Future[Int] {throw new Exception("An error occurred...")}

  eventualInt
    .transform(_ * 10, _ => new Exception("Transformed exception"))
    .onComplete {
      case Success(value) => println(s"Transformed value: $value")
      case Failure(ex) => println(s"Transformed exception: ${ex.getMessage}")
    }

  eventualInt
    .transformWith {
      case Success(value) => Future.successful(value * 10)
      case Failure(_) => Future.failed(new Exception("Transformed exception with transformWith"))
    }
    .onComplete {
      case Success(value) => println(s"TransformWith value: $value")
      case Failure(ex) => println(s"TransformWith exception: ${ex.getMessage}")
    }

  eventualInt.map(_ * 10)
    .onComplete {
      case Success(value) => println(s"Map value: $value")
      case Failure(ex) => println(s"Map exception: ${ex.getMessage}")
    }

  Thread.sleep(5000)
}
