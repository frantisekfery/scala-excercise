package excercise.streams

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.ClosedShape
import akka.stream.scaladsl._
import org.slf4j.Logger

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object ErrorHandlingWithTwoSink extends App {
  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "sys")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  implicit val logger: Logger = system.log

  private val source = Source(-3 to 3)
  private val calculate: Flow[Int, Either[Int, Int], NotUsed] = Flow[Int].map { i =>
    Try(100 / i) match {
      case Success(result) => Right(result)
      case Failure(_) => Left(i)
    }
  }

  private val failureSink: Sink[Either[Int, Int], Future[Seq[Int]]] = Flow[Either[Int, Int]]
    .collect { case Left(failure) => failure }
    .toMat(Sink.seq)(Keep.right)

  private val successSink: Sink[Either[Int, Int], Future[Seq[Int]]] = Flow[Either[Int, Int]]
    .collect { case Right(success) => success }
    .toMat(Sink.seq)(Keep.right)

  private val runnable: RunnableGraph[(Future[Seq[Int]], Future[Seq[Int]])] = RunnableGraph
    .fromGraph(GraphDSL.createGraph(failureSink, successSink)((_, _)) { implicit builder =>
      (failures, successes) =>
        import GraphDSL.Implicits._
        val broadcast = builder.add(Broadcast[Either[Int, Int]](2))

        source ~> calculate ~> broadcast ~> failures
                               broadcast ~> successes
        ClosedShape
    })

  private val (failures, successes) = runnable.run()

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
