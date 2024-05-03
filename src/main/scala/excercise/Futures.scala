package excercise

import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

object Futures extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  // API responses
  private sealed trait APIResponse
  private case class SuccessResponse(code: Int, user: User) extends APIResponse
  private case class ErrorResponse(code: Int, error: String) extends APIResponse
  private case class User(id: Int, name: String)

  // Mock Get User by ID - API endpoint
  private def getUser(id: Int): Future[APIResponse] = {
    val userNames = for {
      firstName <- List("James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Charles", "Thomas")
      surname <- List("Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson")
    } yield firstName + " " + surname
    if (Random.nextBoolean()) {
      Future(SuccessResponse(200, User(id, userNames(id))))
    }
    else Future(ErrorResponse(500, "Server is down, please try later."))
  }

  // List of IDs
  private val ids = (1 to 100).toList

  // Retry
  private def getUserWithRetry(id: Int, retries: Int): Future[APIResponse] = {
    getUser(id).flatMap {
      case response: SuccessResponse => Future.successful(response)
      case _: ErrorResponse if retries > 0 =>
        logger.info(s"Failed to get user $id, retrying")
        getUserWithRetry(id, retries - 1)
      case error: ErrorResponse =>
        Future.successful(error)
    }
  }

  // Sequential processing of future:
  private val sequential: Future[Unit] = ids.foldLeft(Future.successful(())) { (previousFuture, id) =>
    previousFuture.flatMap { _ =>
      getUserWithRetry(id, 15).flatMap {
        case success: SuccessResponse =>
          logger.info(success.user.toString)
          Future.successful(())
        case error: ErrorResponse =>
          logger.info(s"Failed to get user $id: ${error.error}")
          Future.successful(error)
      }
    }
  }

  // Parallel processing of future:
  private val parallel: Future[Seq[Unit]] = Future.traverse(ids) { id =>
    getUserWithRetry(id, 15).map {
      case success: SuccessResponse =>
        logger.info(success.user.toString)
      case error: ErrorResponse =>
        logger.info(s"Failed to get user $id: ${error.error}")
    }
  }

  // Handle completion of the Future
  sequential.onComplete {
    case Success(_) => logger.info("Sequential all done")
    case Failure(e) => logger.info(s"Error: ${e.getMessage}")
  }

  // Handle completion of the Future
  parallel.onComplete {
    case Success(_) => logger.info("Parallel all done")
    case Failure(e) => logger.info(s"Error: ${e.getMessage}")
  }

  Thread.sleep(5000)
}
