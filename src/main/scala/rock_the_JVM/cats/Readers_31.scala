package rock_the_JVM.cats

object Readers_31 {

  case class Configuration(dbUserName:String, dbPassword:String, host: String, port: Int, noThreads:Int)

  case class DBConnection(userName:String, password:String){
    def getOrderStatus(orderId: Long): String = "dispatched "// select sth from DB table
  }

  def main(args: Array[String]): Unit = {

  }
}
