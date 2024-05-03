package excercise

import org.jsoup.Jsoup
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.Materializer
import akka.stream.scaladsl._
import org.slf4j.Logger

import scala.jdk.CollectionConverters._
import scala.concurrent.{ExecutionContextExecutor, Future}

object WebScraper extends App {
  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "sys")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  private val logger: Logger = system.log

  val websites = List("http://www.google.com", "http://www.github.com", "http://www.stackoverflow.com")

  Source(websites)
    .mapAsyncUnordered(parallelism = websites.size) { siteUrl =>
      Future {
        val linksOnPage = Jsoup
          .connect(siteUrl)
          .get()
          .select("a[href]")
          .asScala
          .map(_.attr("abs:href"))
          .toList
        logger.info(s"Fetched ${linksOnPage.size} links from $siteUrl")
        logger.info(s"These links: ${linksOnPage.mkString(", ")}")
        linksOnPage
      }
    }
    .mapConcat(identity)
    .runWith(Sink.ignore)

  system.terminate()
}
