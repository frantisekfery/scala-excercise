package excercise

import org.slf4j.{Logger, LoggerFactory}

object Threads extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private def inceptionThreads(numberOfThreads: Int, currentThread: Int = 1): Thread =
    new Thread(() => {
      if (currentThread < numberOfThreads) {
        val newThread = inceptionThreads(numberOfThreads, currentThread + 1)
        newThread.start()
        newThread.join()
      }
      logger.info(s"I am ${Thread.currentThread().getName}")
    })

  inceptionThreads(100).start()
}

