package excercise.future

import org.slf4j.{Logger, LoggerFactory}

import java.io.IOException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

object FuturesRecoverWith extends App {
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

      if (id == 14) throw new IOException("IO Exception was thrown")
      if (id == 78) throw new RuntimeException("Runtime Exception was thrown")

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
      response <- getUser(id).recoverWith { case ex => Future.successful(ErrorResponse(500, ex.getMessage))}
      result <- response match {
        case success: SuccessResponse => Future.successful(success)
        case error: ErrorResponse if retries == 0 => Future.successful(ErrorResponse(error.code, s"When fetchin user with ID: $id, ${error.error}"))
        case _: ErrorResponse => getUserWithRetry(id, retries - 1)
      }
    } yield result

  private val emptyResponseListAsFuture = Future.successful(List.empty[APIResponse])

  ids.foldLeft(emptyResponseListAsFuture) { (accumulatedResponsesFuture, id) => for {
        accumulatedResponses <- accumulatedResponsesFuture
        apiResponse <- getUserWithRetry(id)
      } yield apiResponse match {
        case success: SuccessResponse => accumulatedResponses :+ success
        case error: ErrorResponse => accumulatedResponses :+ error
      }
    }
    .onComplete {
      case Success(value) =>
        logger.info("Sequential 2 all done")
        logger.info(s"${value.mkString("Values are:\n", "\n","")}")
      case Failure(e) => logger.info(s"Error: ${e.getMessage}")
    }

  Thread.sleep(5000)
}
