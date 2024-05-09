package excercise.future

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
    Future {
      val userNames = for {
        firstName <- List("James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Charles", "Thomas")
        surname <- List("Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson")
      } yield firstName + " " + surname

      if (id == 78) throw new RuntimeException("something has happened")

      if (Random.nextBoolean()) SuccessResponse(200, User(id, userNames(id)))
      else ErrorResponse(500, "Server is down, please try later.")
    }
  }

  // List of IDs
  private val ids = (0 to 99).toList

  // Retry attempts
  private val retryAttempts = 3

  // Retry
  private def getUserWithRetry(id: Int, retries: Int = retryAttempts): Future[APIResponse] = for {
      response <- getUser(id)
      result <- response match {
        case success: SuccessResponse => Future.successful(success)
        case error: ErrorResponse if retries == 0 => Future.successful(error)
        case _: ErrorResponse => getUserWithRetry(id, retries - 1)
      }
    } yield result

  private val emptyResponseListAsFuture = Future.successful(List.empty[APIResponse])

  // Sequential processing of future:
  private val sequential: Future[List[APIResponse]] = ids.foldLeft(emptyResponseListAsFuture) { (accResponsesF, id) =>
    accResponsesF.flatMap { acc =>
      getUserWithRetry(id).flatMap {
        case success: SuccessResponse => Future.successful(acc :+ success)
        case error: ErrorResponse => Future.successful(acc :+ error)
      }
    }
  }

  // Sequential processing of future:
  private val sequential2: Future[List[APIResponse]] = ids.foldLeft(emptyResponseListAsFuture) { (accumulatedResponsesFuture, id) =>
    for {
      accumulatedResponses <- accumulatedResponsesFuture
      apiResponse <- getUserWithRetry(id)
    } yield apiResponse match {
      case success: SuccessResponse =>
        logger.info(s"Everything is just fine with ID: ${success.user.id}.")
        accumulatedResponses :+ success
      case error: ErrorResponse =>
        logger.error(s"We have an issue here with this message: ${error.error}.")
        accumulatedResponses :+ error
    }
  }

  // Parallel processing of future:
  private val parallel: Future[Seq[APIResponse]] = Future.traverse(ids) { id =>
    getUserWithRetry(id).map {
      case success: SuccessResponse => success
      case error: ErrorResponse => error
    }
  }

  // Handle completion of the Future
  sequential.onComplete {
    case Success(value) =>
      logger.info("Sequential 1 all done")
      logger.info(s"Values are: ${value.mkString(", ")}")
    case Failure(e) => logger.info(s"Error: ${e.getMessage}")
  }

  // Handle completion of the Future
  sequential2.onComplete {
    case Success(value) =>
      logger.info("Sequential 2 all done")
      logger.info(s"Values are: ${value.mkString(", ")}")
    case Failure(e) => logger.info(s"Error: ${e.getMessage}")
  }

  // Handle completion of the Future
  parallel.onComplete {
    case Success(value) =>
      logger.info("Parallel all done")
      logger.info(s"Values are: ${value.mkString(", ")}")
    case Failure(e) => logger.info(s"Error: ${e.getMessage}")
  }

  Thread.sleep(5000)
}
