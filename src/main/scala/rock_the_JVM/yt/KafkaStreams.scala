package rock_the_JVM.yt

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.{GlobalKTable, JoinWindows, TimeWindows, Windowed}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.{KGroupedStream, KStream, KTable}
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import rock_the_JVM.yt.KafkaStreams.Domain._
import rock_the_JVM.yt.KafkaStreams.Topics.{DiscountProfilesByUserTopic, DiscountsTopic, OrdersByUserTopic}

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Properties
import scala.concurrent.duration._

case object KafkaStreams {

  object Domain {
    type UserId = String
    type Profile = String
    type Product = String
    type OrderId = String
    type Status = String
  }

  case class Order(orderId: OrderId, userId: UserId, products: List[Product], amount: BigDecimal)

  case class Discount(profile: Profile, amount: BigDecimal)

  case class Payment(orderId: OrderId, status: Status)

  object Topics {
    final val OrdersByUserTopic = "orders-by-user"
    final val DiscountProfilesByUserTopic = "discount-profiles-by-user"
    final val DiscountsTopic = "discounts"
    final val OrdersTopic = "orders"
    final val PaymentsTopic = "payments"
    final val PaidOrdersTopic = "paid-orders"
  }

  // automatic serializing / deserializing entity when writing to / consuming from a topic
//  implicit def serdeOder[A >: : Decoder : Encoder]: Serde[A] = {
//    val serializer = (entity: A) => entity.asJson.noSpaces.getBytes
//    val deserializer = (bytes: Array[Byte]) => {
//      val string = new String(bytes)
//      decode[A](string).toOption
//    }
//
//    Serdes.fromFn[A](serializer, deserializer)
//  }
//
//  // topology
//  val builder = new StreamsBuilder
//
//  // KStream
//  val userOrdersStream: KStream[UserId, Order] = builder.stream[UserId, Order](OrdersByUserTopic)
//
//  // KTable - is distributed
//  val userProfilesTable: KTable[UserId, Profile] = builder.table[UserId, Profile](DiscountProfilesByUserTopic)
//
//  // GlobalKTable - copied to all the nodes
//  val discountProfilesGTable: GlobalKTable[Profile, Discount] =
//    builder.globalTable[Profile, Discount](DiscountsTopic)
//
//  // KStream transformation
//  val expensiveOrders: KStream[UserId, Order] = userOrdersStream.filter { (userId, order) =>
//    order.amount >= 1000
//  }
//
//  val purchasedListOfProductsStream: KStream[UserId, List[Product]] = userOrdersStream.mapValues { order =>
//    order.products
//  }
//
//  val purchasedProductsStream: KStream[UserId, Product] = userOrdersStream.flatMapValues { order =>
//    order.products
//  }
//
//  // join
//  val ordersWithUserProfileStream: KStream[UserId, (Order, Profile)] =
//    userOrdersStream.join[Profile, (Order, Profile)](userProfilesTable) { (order, profile) =>
//      (order, profile)
//    }
//
//  builder.build()
//
//  val kafkaCreateTopicsCommand = List(
//    "orders-by-user",
//    "discount-profiles-by-user",
//    "discounts",
//    "orders",
//    "payments",
//    "paid-orders"
//  ).foreach(topic =>
//    println(s"kafka-topics --bootstrap-server localhost:9092 --topic $topic --create ")
//  )
//

}
