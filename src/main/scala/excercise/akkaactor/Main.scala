package excercise.akkaactor

import akka.NotUsed
import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import excercise.akkaactor.actors.{ChatRoom, Gabbler}

object Main  extends App {

  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val chatRoom = context.spawn(ChatRoom(), "chatroom")
      context.log.info("1. Chatroom was created.")
      val gabblerRef = context.spawn(Gabbler(), "gabbler")
      context.log.info("2. Gabbler was created.")
      context.watch(gabblerRef)
      chatRoom ! ChatRoom.GetSession("ol’ Gabbler", gabblerRef)

      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }

    ActorSystem(Main(), "ChatRoomDemo")
}