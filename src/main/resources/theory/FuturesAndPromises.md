# Futures and Promises

## Callbacks
Callbacks are functions that are passed as parameters to other functions and are invoked after certain actions have been 
completed. In Scala, you might use callbacks in various situations, especially for event handling or for handling 
completion of asynchronous tasks. However, one of the challenges with callbacks is, if used extensively, they lead to 
complex nested code that becomes hard to maintain and reason about, often referred to as "callback hell".

Here are a few examples to illustrate how callbacks can be used in Scala:
**Example 1 — Higher Order Functions**: Example of a callback is passing a function as a parameter (sometimes called 
higher-order functions).
```scala
def printAfterOperation(x: Int, y: Int, operation: (Int, Int) => Int): Unit = {
  val result = operation(x, y)
  println("Result is: " + result)
}

printAfterOperation(5, 10, (a, b) => a + b) // prints "Result is: 15"
printAfterOperation(5, 10, (a, b) => a * b) // prints "Result is: 50"
```
In the above example, the function operation is a callback function that gets called inside the printAfterOperation 
function.

**Example 2 — Asynchronous Computation**: Callbacks are often used to manage asynchronous operations.
Suppose you have an asynchronous function that fetches some data from a database.
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

def performOperationOnUser(callback: Option[String] => Unit): Unit = 
  Future(callback(getUserFromDB()))

// usage
performOperationOnUser(user => println(user.getOrElse("No data"))) 
```
Here fetchData is a function that initiates an asynchronous operation. The callback is a function that it will call once
the operation is completed. This pattern is often used in APIs that fetch data asynchronously.

Remember that these are simple examples of callbacks. In real-world applications, callbacks could be more complex,
especially when dealing with error handling and edge cases. Also, too much reliance on deeply nested callbacks can lead 
to code that's hard to read and understand (a situation often referred to as "callback hell"). Hence the use of Future 
and Promises to make asynchronous code easier to reason about and maintain.

## Futures 
Futures provide a way to reason about performing many operations in parallel in an efficient and non-blocking way. 
A Future is a placeholder object for a value that may not yet exist. It is a way to represent a value that is initially 
unknown but becomes fulfilled at some point, with the value it will contain. Future is used for computations which might 
not have completed yet, and Future also makes it easier to reason about concurrent and parallel operations.

### Methods 
- **map**: Transforms the result of a future using a function when the future is completed successfully.
```scala
Future.successful(21).map(_ * 2) // Future[Int] with value 42
```
- **flatMap**: A way of chaining futures sequentially. It chains two futures so the second future is computed based on 
the completed result of the first future or to get rid of nested future: Future[Future[A]]
```scala
Future.successful(21).flatMap(v => Future.successful(v * 2)) // Future[Int] with value 42
```
- **filter**: Filters the future result. If the predicate does not hold, it will return a Future with 
NoSuchElementException.
```scala
Future.successful(42).filter(_ % 2 == 0) // Future[Int] with value 42
Future.successful(42).filter(_ % 2 == 1) // Future Failure with NoSuchElementException
```
- **recover**: Creates a new future by applying a function to the failed result of the source future. If the source is 
a success, the new future will also be a success.
```scala
Future.failed(new Exception("Failed")).recover { case _ => 42 } // Future[Int] with value 42
```
- **recoverWith**: Recovers a future with another future in case of failure. If the source is a success, the new future
will also be a success.
```scala
Future.failed(new Exception("Failed")).recoverWith { case _ => Future.successful(42) } // Future[Int] with value 42
```
- **foreach**: Takes a procedure (a function with a result type of Unit) and applies it if the future completes 
successfully. Used for side effects.
```scala
Future.successful(42).foreach(v => println(v)) // Prints 42
```
- **onComplete**: Used to perform some side effect once the Future is completed.
```scala
Future.successful(42).onComplete {
  case Success(v) => println(v)
  case Failure(_) => println("Failed")
} // Prints 42
```
- **andThen**: Lets you sequence side effects (e.g. logging). The original future will continue on regardless of this 
side effect.
```scala
Future.successful(42).andThen { case v => println(v) } // Prints Success(42)
```
- **transform**: Creates a new Future by applying two functions to the result or the exception of the original Future.
```scala
// Returns Future[Int] with value 42
Future.successful(21).transform(_ * 2, _ => new Exception("Transformed Failure")) 
```
- **transformWith**: Like transform, but with a function returning a Future.
```scala
Future.successful(21).transformWith {
  case Success(v) => Future.successful(v * 2)
  case _ => Future.failed(new Exception("Transformed Failure"))
} // Returns Future[Int] with value 42
```
- **zip**: Pairs two futures, returning a new Future holding a tuple with the results of both original futures, if they 
are both successful.
```scala
Future.successful(21).zip(Future.successful(2)) // Returns Future[(Int, Int)] with value (21, 2)
```
- **fallbackTo**: Creates a new Future that will hold the successful result of the original future or, if the original 
future fails, the successful result of the fallback future.
```scala
Future.failed(new Exception("Failed")).fallbackTo(Future.successful(42)) // Returns Future[Int] with value 42
```
- **failed**: Returns a new Future holding the error of the original Future if it fails. If the original is successful
then the returned future fails with a NoSuchElementException.
```scala
Future.failed(new Exception("Failed")).failed // Returns Future[Throwable] with the original exception
```
- **zipWith**: Combines two futures using a combining function.
```scala
Future.successful(21).zipWith(Future.successful(2))(_ * _) // Returns Future[Int] with value 42
```

## Promises
Promises in Scala are writable, single-assignment containers, which complete a Future. While a Future is a read-only 
reference to a value which might not yet be available, a Promise can be thought of as a writeable, single-assignment 
container, which completes a Future. Essentially, the Promise enables you to complete a Future.

## Use-cases for Futures and Promises

1) **Bridge Over Non-Futures Supported APIs**: This comes into play when APIs do not natively support Futures. These are 
usually callback-based or blocking APIs. You can leverage promises to convert these APIs into a Future-based API.
```scala
import scala.concurrent.{Promise, Future}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

def callbackBasedAPI(query: String, callback: Either[Throwable, String] => Unit): Unit = {
// ... some asynchronous operation, such as making a network call
}

def futureBasedAPI(query: String): Future[String] = {
   val promise = Promise[String]()
   callbackBasedAPI(query, {
      case Left(ex) => promise.failure(ex)
      case Right(data) => promise.success(data)
   })
   promise.future
}
```
2) **Parallel Computations**: This makes it possible to carry out several computations in parallel, creating a Future 
for each, and then combining their results as soon as they are available. With Future you can write higher level 
operations like map, filter, and reduce, which can handle the thread management for you.
```scala
val future1 = Future { operation1() }
val future2 = Future { operation2() }

val result = for {
   r1 <- future1
   r2 <- future2
} yield (r1, r2)

result.foreach { case (r1, r2) => println(s"Results: $r1, $r2") }
```
3) **Timeouts**: You can use Promise for setting up a timeout for a Future's operation.
```scala
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

val computation: Future[Int] = ??? //... doing some calculation

val timeout = Promise[Int]()

Future {
   Thread.sleep(1000) // Delay of 1 second
   timeout.success(-1)
}

Future
   .firstCompletedOf(Seq(computation, timeout.future))
   .foreach {
      case -1 => println("Computation timed out!")
      case res => println(s"Result of computation: $res")
   }
```
4) **Communicating Between Threads**: Promises can be used as a one-time communication mechanism between different 
threads, particularly to communicate the result of a computation carried out by a worker thread to another thread.
```scala
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

val promise = Promise[Int]()
new Thread(() => {
   val result = computeSomething() // This function does some computation
   promise.success(result) // Completing the promise with a value
}).start()

// On a different thread/location in code
promise.future.foreach(result => println(s"Result from another thread: $result"))
```
