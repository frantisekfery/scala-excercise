package excercise

import java.time.Duration
import java.time.Instant
import org.slf4j.{Logger, LoggerFactory}

object Anagrams extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  // find anagrams in list of strings
  val words = List("army", "Mary", "tyrant", "rantty", "debit card", "bad credit", "auctioned", "cautioned",
    "fortune", "counterfeit", "The Morse code", "Here come dots", "Eleven plus two", "Twelve plus one",
    "School master", "The classroom", "Listen", "Silent", "Astronomer", "Moon starer", "The earthquakes",
    "That queer shake", "The public art galleries", "Large picture halls, I bet", "Conversation",
    "Voices rant on", "A telescope", "To see place", "The eyes", "They see", "A gentleman", "Elegant man", "An artist",
    "Transit", "Slot machines", "Cash lost in em", "Fourth of July", "Joyful Fourth", "The eyes", "They See")

  val times = 10
  val averageTime1 = (1 to times).map { _ =>
      val t0 = Instant.now
      words
        .groupBy(_.toLowerCase.filter(_.isLetter).sorted)
        .values.filter(words => words.size > 1)
        .mkString("\n")
      val t1 = Instant.now
      Duration.between(t0, t1)
    }
    .map(_.toMillis)
    .sum.toDouble / times

  val averageTime2 = (1 to times).map { _ =>
      val t2 = Instant.now
      words
        .groupBy(_.toLowerCase.filter(_.isLetter).sorted)
        .collect { case (_, value) if value.size > 1 => value }
        .mkString("\n")
      val t3 = Instant.now
      Duration.between(t2, t3)
    }
    .map(_.toMillis)
    .sum.toDouble / times

  // Calculate average times
  logger.info(s"Average time taken by method 1 is $averageTime1 millis")
  logger.info(s"Average time taken by method 2 is $averageTime2 millis")
}
