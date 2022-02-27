package rock_the_JVM.cats.chapter_3

import cats.Id
import cats.data.Reader

object Readers_31 {

  case class Configuration(dbUserName: String, dbPassword: String, host: String, port: Int, noThreads: Int)

  case class DBConnection(userName: String, password: String) {
    def getOrderStatus(orderId: Long): String = "dispatched " // select sth from DB table

    def lastOrderId(userName: String): Long = 12123
  }

  case class HttpService(host: String, port: Int) {
    def start(): Unit = println("Server started") // simplified implementation
  }

  // in real life fetch config from file
  val config = Configuration("user", "pass", "localhost", 1234, 0)

  // Reader is a data processing type
  val dbReader: Reader[Configuration, DBConnection] = Reader(conf =>
    DBConnection(conf.dbUserName, conf.dbPassword)
  )
  val dbConn: Id[DBConnection] = dbReader.run(config)

  // use map transformations of output
  val orderStatusReader: Reader[Configuration, String] = dbReader.map(dbConn => dbConn.getOrderStatus(orderId = 33))
  val orderStatus: String = orderStatusReader.run(config)

  /**
   *
   * Pattern for Reader usage:
   * - you create the initial data structure
   * -  you create a reader which specifies how that data structure will be manipulated later
   * - you can then map && flatMap the reader to produce derived information
   * - when you need the final pieces of information, you call run on the reader with the initial data structure
   */


  // More general example - readers compositions
  def getLastOrderStatus(userName: String): String = {
    val lastOrderIdReader: Reader[Configuration, String] = dbReader.map(_.lastOrderId(userName))
      .flatMap(lastOrderId => dbReader.map(_.getOrderStatus(lastOrderId)))

    // identical
    val usersOrderFor = for {
      lastOrderId <- dbReader.map(_.lastOrderId(userName))
      orderStatus <- dbReader.map(_.getOrderStatus(lastOrderId))
    } yield orderStatus

    lastOrderIdReader.run(config)


  }

//  val usersOrderFor = for {
//    lastOrderId <- dbReader.map(_.lastOrderId("asdas"))
//
//  }

  def main(args: Array[String]): Unit = {


  }
}
