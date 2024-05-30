package excercise

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object B extends App {

  def doWork(): Int = {
    Thread.sleep(1000)
    35
  }

  val promise = Promise[Int]()
  new Thread(() => {
    val x = doWork() // Some computation performed in a new thread
    promise.success(x) // Pass the computed value back to main thread
  }).start()

  promise.future.foreach(println) // reacts when the value is set in the new thread
}
