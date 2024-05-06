package excercise.streams

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}
import akka.{Done, NotUsed}
import org.slf4j.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object AkkaStreams extends App {

  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "app")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  private val logger: Logger = system.log
  
  private val source: Source[Int, NotUsed] = Source(1 to 1000)

  var output1 = ListBuffer[(Int, Int)]()
  var output2 = ListBuffer[(Int, Int)]()

  // Easy example
  private val eventuallyEasyExampleDone: Future[Done] = source
    .via(Flow[Int].map(x => (x + 1, x * 2)))
    .runWith(Sink.foreach[(Int, Int)](tupple => {
      logger.info(tupple.toString())
      output1.addOne(tupple)
    }))

  // Advanced example with 2 computations which cannot be done with just one flow
  val eventuallyAdvancedExampleDone = RunnableGraph.fromGraph(GraphDSL.createGraph(Sink.foreach[(Int, Int)](tupple => {
    logger.info(tupple.toString())
    output2.addOne(tupple)
  })) { implicit builder =>
    s =>
      import GraphDSL.Implicits._

      val incrementer = builder.add(Flow[Int].map(x => x + 1))
      val multiplier = builder.add(Flow[Int].map(x => x * 2))

      val broadcast = builder.add(Broadcast[Int](2))
      val zip = builder.add(Zip[Int, Int])

      source ~> broadcast
      broadcast.out(0) ~> incrementer ~> zip.in0
      broadcast.out(1) ~> multiplier  ~> zip.in1
      zip.out ~> s

      ClosedShape
  }).run()

  Future.sequence(List(eventuallyEasyExampleDone, eventuallyAdvancedExampleDone)).onComplete {
    case Success(_) =>
      logger.info("Both streams completed successfully.")
      logger.info(s"Streams are${if (output1 != output2) " not" else ""} the same.")
      system.terminate()

    case Failure(e) =>
      logger.info(s"One or both streams failed with message: ${e.getMessage}")
  }}
