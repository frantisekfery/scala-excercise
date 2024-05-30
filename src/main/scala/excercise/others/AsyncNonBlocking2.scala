package excercise

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

object AsyncNonBlocking2 extends App {

  val promise: Promise[String] = Promise[String]()
  val futureFromPromise: Future[String] = promise.future

  println(s"Promise created at ${System.currentTimeMillis()} in thread ${Thread.currentThread().getName}")
  Thread.sleep(2000)

  futureFromPromise.onComplete { result =>
    println(s"Promise completed at ${System.currentTimeMillis()} in thread ${Thread.currentThread().getName}")
    println(result)
  }

  println(s"Promise set success at ${System.currentTimeMillis()} in thread ${Thread.currentThread().getName}")
  promise.success("Hello, Promise/Future!")
  Thread.sleep(2000)

  val future: Future[Int] = Future {
    Thread.sleep(1000) // Simulate a long-running task
    42
  }

  // Register callback to print the result when it is available
  future.onComplete {
    case Success(number) => println(s"The answer is $number")
    case _ => println("Promise wasn't kept.")
  }

  val myPromise: Promise[Int] = Promise[Int]()
  val futureResult: Future[Int] = myPromise.future

  // You have control over when and how your promise is completed
  myPromise.success(42)

  // This will print "The answer is 42" because you completed your promise with a value of 42
  futureResult.onComplete {
    case Success(number) => println(s"The answer is $number")
    case _ => println("Promise wasn't kept.")
  }
}