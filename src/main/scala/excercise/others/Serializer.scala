package excercise

import akka.serialization.Serializer
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.{Logger, LoggerFactory}

object Serializer extends App {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private object JsonSerializer extends Serializer {
    override def identifier: Int = 1234567
    override def includeManifest: Boolean = true

    override def toBinary(o: AnyRef): Array[Byte] = {
      o match {
        case person: Person => person.asJson.noSpaces.getBytes
        case address: Address => address.asJson.noSpaces.getBytes
        case _ => throw new IllegalArgumentException("Unknown type.")
      }
    }

    override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
      manifest match {
        case Some(cls) if cls == classOf[Person] =>
          decode[Person](new String(bytes)) match {
            case Right(person) => person
            case Left(error) => throw new RuntimeException(error)
          }
        case Some(cls) if cls == classOf[Address] =>
          decode[Address](new String(bytes)) match {
            case Right(address) => address
            case Left(error) => throw new RuntimeException(error)
          }
        case _ => throw new IllegalArgumentException("Unknown manifest.")
      }
    }
  }

  private object AvroSerializer extends Serializer {
    override def identifier: Int = 12345

    override def includeManifest: Boolean = true

    override def toBinary(o: AnyRef): Array[Byte] = {
      o match {
        case person: Person =>
          val outputStream = new java.io.ByteArrayOutputStream()
          val output = AvroOutputStream.binary[Person].to(outputStream).build()
          output.write(person)
          output.flush()
          output.close()
          outputStream.toByteArray
        case address: Address =>
          val outputStream = new java.io.ByteArrayOutputStream()
          val output = AvroOutputStream.binary[Address].to(outputStream).build()
          output.write(address)
          output.flush()
          output.close()
          outputStream.toByteArray
        case _ => throw new IllegalArgumentException("Unknown type.")
      }
    }

    override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
      manifest match {
        case Some(cls) if cls == classOf[Person] =>
          val schema = AvroSchema[Person]
          val in = AvroInputStream.binary[Person].from(bytes).build(schema)
          in.iterator.next()
        case Some(cls) if cls == classOf[Address] =>
          val schema = AvroSchema[Address]
          val in = AvroInputStream.binary[Address].from(bytes).build(schema)
          in.iterator.next()
        case _ => throw new IllegalArgumentException("Unknown manifest.")
      }
    }
  }

  private case class Person(name: String, age: Int)
  private case class Address(country: String, city: String, street: String, number: Int)

  private val person = Person("Joe", 35)
  logger.info(person.toString)
  private val serializedPerson = JsonSerializer.toBinary(person)
  logger.info(serializedPerson.mkString("ByteArray(", ",", ")"))
  private val deserializedPerson = JsonSerializer.fromBinary(serializedPerson, Some(classOf[Person]))
  logger.info(deserializedPerson.toString)

  private val address = Address("Slovakia", "Kosice", "Sturova", 2)
  logger.info(address.toString)
  private val serializedAddress = JsonSerializer.toBinary(address)
  logger.info(serializedAddress.mkString("ByteArray(", ",", ")"))
  private val deserializedAddress = JsonSerializer.fromBinary(serializedAddress, Some(classOf[Address]))
  logger.info(deserializedAddress.toString)

  private val person2 = Person("Joe", 35)
  logger.info(person2.toString)
  private val serializedPerson2 = AvroSerializer.toBinary(person2)
  logger.info(serializedPerson2.mkString("ByteArray(", ",", ")"))
  private val deserializedPerson2 = AvroSerializer.fromBinary(serializedPerson2, Some(classOf[Person]))
  logger.info(deserializedPerson2.toString)

  private val address2 = Address("Slovakia", "Kosice", "Sturova", 2)
  logger.info(address2.toString)
  private val serializedAddress2 = AvroSerializer.toBinary(address)
  logger.info(serializedAddress2.mkString("ByteArray(", ",", ")"))
  private val deserializedAddress2 = AvroSerializer.fromBinary(serializedAddress2, Some(classOf[Address]))
  logger.info(deserializedAddress2.toString)

  // According my comparison avro seems efficient when comparing Byte array outputs from both.
}
