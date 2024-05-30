package excercise

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object AsyncNonBlocking extends App {
  println("Starting")

  val promiseResolver = ActorSystem(Behaviors.receiveMessage[(String, Promise[Int])] {
      case (message, promise) =>
        promise.success(message.length)
        Behaviors.same
  }, "promiseResolver")

  def doAsyncNonBlockingThing(arg: String): Future[Int] = {
    val aPromise = Promise[Int]()
    promiseResolver ! (arg, aPromise)
    aPromise.future
  }

  val asyncNonBlockingResult = doAsyncNonBlockingThing("Message")
  asyncNonBlockingResult.onComplete(value => println(s"I've got a non-blocking async answer: $value"))
  promiseResolver.terminate()
}
