package excercise

import org.slf4j.{Logger, LoggerFactory}

object Anagrams extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  // find anagrams in list of strings
  private val list = List("abc", "acb", "bc", "ca", "cab", "akb", "kab", "fff")
  // result
  logger.info(list.groupBy(_.sorted).values.filter(list => list.size > 1).toString())
}

