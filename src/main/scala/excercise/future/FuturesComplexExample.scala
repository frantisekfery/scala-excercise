package excercise.future

import excercise.future.FuturesComplexExample.ExternalService.loggerES
import excercise.future.FuturesComplexExample.InternalService.loggerIS
import org.slf4j.{Logger, LoggerFactory}

import java.io.IOException
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

object FuturesComplexExample extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  case class User(id: Option[String], name: String)

  sealed trait APIResponse
  case class SuccessResponse(code: Int, user: User) extends APIResponse
  case class ErrorResponse(code: Int, error: String) extends APIResponse

  sealed trait AuditResponse
  case class AuditSuccess() extends AuditResponse

  object ExternalService {
    val loggerES: Logger = LoggerFactory.getLogger(this.getClass)
  }

  case class ExternalService() {
    val counter = new AtomicInteger(0)

    def createUser(user: User): Future[APIResponse] = Future {
//      throw new IOException("IO Exception was thrown")
      var wasThere = true
      val currentCount = counter.getAndIncrement()

      if (currentCount == 14) throw new IOException("IO Exception was thrown")
      if (currentCount == 78 && wasThere) throw new RuntimeException("Runtime Exception was thrown")
      if (currentCount == 78) wasThere = false

      Thread.sleep(Random.nextInt(1000))

      if (randomWithMoreTrues()) {
        loggerES.info(s"User ${user.name} was created.")
        SuccessResponse(200, user)
      } else {
        loggerES.info(s"User ${user.name} was not created.")
        ErrorResponse(500, "Server is down, please try later.")
      }
    }

    private def randomWithMoreTrues(): Boolean = {
      val number = Random.nextInt(10)
      number <= 7 // Returns true approximately 80% of the time
    }

    def auditLog(result: List[APIResponse]): Future[AuditResponse] = {
      loggerES.info(s"Audit log for was created.")
      Future.successful(AuditSuccess())
    }
  }

  object InternalService {
    val loggerIS: Logger = LoggerFactory.getLogger(this.getClass)
  }

  case class InternalService() {
    def getUsers: List[User] = {
      for {
        f <- List("James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Charles", "Thomas")
        l <- List("Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson")
      } yield {
        val user = User(Some(UUID.randomUUID().toString), s"$f $l")
        loggerIS.info(s"User $user was constructed.")
        user
      }
    }

    private def createUserWithRetry(service: ExternalService, user: User, retries: Int = 3): Future[APIResponse] =
      for {
        response <- service.createUser(user)
        result <- response match {
          case success: SuccessResponse =>
            loggerIS.info(s"Successfully created user ${success.user}.")
            Future.successful(success)
          case error: ErrorResponse if retries == 0 =>
            loggerIS.info(s"Retry was exhausted, sending ErrorResponse.")
            Future.successful(ErrorResponse(error.code, s"When fetching user with ID: $user.id, ${error.error}"))
          case _: ErrorResponse =>
            loggerIS.info(s"Service got error while creating user, retrying...")
            createUserWithRetry(service, user, retries - 1)
        }
      } yield result

    def notifyAuditWhenUsersCreated(service: ExternalService, users: List[User]): Future[AuditResponse] = {
      Future.traverse(users) { u =>
        service.createUser(u).recoverWith {
          case ex: IOException =>
            loggerIS.info(s"IO Exception arise for user ${u.name}.")
            Future.successful(ErrorResponse(500, s"${ex.getMessage} for user ${u.name}"))
          case _: RuntimeException =>
            loggerIS.info(s"Runtime Exception arise for user ${u.name}.")
            createUserWithRetry(service, u)
        }
      }.flatMap(responses => service.auditLog(responses))
    }
  }

  private val service: InternalService = InternalService()
  private val users: List[User] = service.getUsers
  service.notifyAuditWhenUsersCreated(ExternalService(), users)
    .onComplete {
      case Success(value) => logger.info(s"Audit Result is $value")
      case Failure(exception) => logger.info(s"Failure is $exception")
    }

  Thread.sleep(15_000)
}
