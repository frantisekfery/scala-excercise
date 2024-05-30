package excercise.future

import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

object FuturesWithoutRecovering extends App {
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

  private val emptyResponseListAsFuture = Future.successful[List[APIResponse]](List())

  // Sequential processing of future:
  private val sequential: Future[List[APIResponse]] = ids.foldLeft(emptyResponseListAsFuture) { (accResponsesF, id) =>
    accResponsesF.flatMap { accResponses =>
      getUserWithRetry(id).flatMap {
        case success: SuccessResponse => Future.successful(accResponses :+ success)
        case error: ErrorResponse => Future.successful(accResponses :+ error)
      }
    }
  }

  // Sequential processing of future with for-comprehension
  private val sequential2: Future[List[APIResponse]] = ids.foldLeft(emptyResponseListAsFuture) { (accResponsesF, id) =>
    for {
      accResponses <- accResponsesF
      apiResponse <- getUserWithRetry(id)
    } yield apiResponse match {
      case success: SuccessResponse => accResponses :+ success
      case error: ErrorResponse => accResponses :+ error
    }
  }

  // Parallel processing of future:
  private val parallel: Future[List[APIResponse]] = Future.traverse(ids)(getUserWithRetry)


  private val parallel2: Future[List[APIResponse]] = Future.sequence(ids.map(id => getUserWithRetry(id)))

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


  Future.traverse(ids) { id =>
    getUserWithRetry(id)
      .map(res => (id, res))
      .recoverWith {
        case ex: Throwable => Future.successful((id, ErrorResponse(500, ex.getMessage)))
      }
  }.foreach {
    value => {
      println(s"Successful ids are: ${value.filter(_._2.isInstanceOf[SuccessResponse]).map(_._1)}")
      println(s"Problematic ids are: ${value.filter(_._2.isInstanceOf[ErrorResponse])}")
    }
  }

  ids.foldLeft(Future.successful[List[(Int, APIResponse)]](List())) {
    (accFuture, id) => for {
      acc <- accFuture
      result <- getUserWithRetry(id).map(response => (id, response))
        .recoverWith { case ex: Throwable => Future.successful(id, ErrorResponse(500, ex.getMessage)) }
    } yield acc :+ result
  }
    .map(value => value.filter(_._2.isInstanceOf[ErrorResponse]).map(_._1))
    .onComplete(println)

  Thread.sleep(5000)
}
