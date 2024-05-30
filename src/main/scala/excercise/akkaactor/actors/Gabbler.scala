package excercise.akkaactor.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}

object Gabbler {
  import ChatRoom._

  def apply(): Behavior[SessionEvent] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SessionGranted(handle) =>
          context.log.info("4. Gabbler received Session Granted.")
          handle ! PostMessage("Hello World!")
          Behaviors.same
        case MessagePosted(screenName, message) =>
          context.log.info2("8. Message has been posted by '{}': {}", screenName, message)

          Behaviors.stopped
      }
    }
}