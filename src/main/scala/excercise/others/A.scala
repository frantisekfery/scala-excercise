package excercise

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object A extends App {

  println(Thread.currentThread().getName)
  Future {
    println(Thread.currentThread().getName)
    Thread.sleep(1000)
    10
  }.onComplete{
    case Success(value) => println(s"Got the callback with value: $value")
    case Failure(exception) => println(s"Got an exception: ${exception.getMessage}")
  }

  Thread.sleep(2000)
}
