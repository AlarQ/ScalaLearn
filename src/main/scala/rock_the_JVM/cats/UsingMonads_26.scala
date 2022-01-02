package rock_the_JVM.cats

import akka.stream.StreamRefMessages.Payload
import cats.Monad
import rock_the_JVM.cats.UsingMonads_26.LoadingOr

object UsingMonads_26 {

  // from last lessons...
  val monadList: Monad[List] = Monad[List] // fetch the implicit Monad[List]
  val aSimpleList: List[Int] = monadList.pure(2)
  val anExtendedList: Seq[Int] = monadList.flatMap(aSimpleList)(x => List(x, x + 1))
  // applicable t oOption, Try, etc...

  // Either is also a monad
  val aManualEither: Either[String, Int] = Right(42)
  // String - loading message, T - desired value
  type LoadingOr[T] = Either[String, T]
  type ErrorOr[T] = Either[Throwable, T]

  // Let's see monad ops for new defined types
    val loadingMonad: Monad[LoadingOr] = Monad[LoadingOr]
    val anEither: LoadingOr[Int] = loadingMonad.pure(45) // Right(45)
    val aChangeLoading: LoadingOr[Int] = loadingMonad.flatMap(anEither)(x => if (x > 0) Right(45) else Left("loading...."))

    // imaginary online store
    case class OrderStatus(orderId: Long, status: String)

    def getOrderStatus(orderId: Long): LoadingOr[OrderStatus] = Right(OrderStatus(orderId, status = "ready to ship."))

    def tracLocation(orderStatus: OrderStatus): LoadingOr[String] = {
      if (orderStatus.orderId > 1000) Left("Not available yet, refreshing data...")
      else Right("Amsterdam, NL")
    }

    val orderId = 450L
    val orderLocation: LoadingOr[String] = loadingMonad.flatMap(getOrderStatus(orderId))(orderStatus => tracLocation(orderStatus))

    val orderLocationBetter: LoadingOr[String] = getOrderStatus(orderId).flatMap(orderStatus => tracLocation(orderStatus))

    val orderLocationFor: LoadingOr[String] = for {
      orderStatus <- getOrderStatus(orderId)
      location <- tracLocation(orderStatus)
    } yield location

  case class Connection(host:String,port:String)

  // TODO exercises - service layer
  trait HttpService[M[_]]{
    def getConnection(cfg:Map[String,String]): M[Connection]
    def issueRequest(connection: Connection,payload:String): M[String]
  }
//// TODO czemu to nie działa
//  def getResponse[M[_]](service: HttpService[M],payload: String)(implicit monad:Monad[M]): M[String] =
//    for{
//      conn <- service.getConnection(config)
//      response <- service.issueRequest(conn,payload)
//    } yield response

  object OptionService extends HttpService[Option] {
    override def getConnection(cfg: Map[String, String]): Option[Connection] =
      for{
        h <- cfg.get("host")
        p <- cfg.get("port")
      } yield Connection(h,p)

    override def issueRequest(connection: Connection, payload: String): Option[String] = {
      if(payload.length > 20) None
      else Some(s"Request ($payload) has been accepted")
    }
  }



  val config = Map(
    "host" -> "localhost",
    "port" -> "10002"
  )

  def main(args: Array[String]): Unit = {
    val responseOption =  OptionService.getConnection(config).flatMap{
      conn => OptionService.issueRequest(conn,"Hello HTPP service")
    }
    // same using for-comprehension
    val responseOptionFor = for{
      connection <- OptionService.getConnection(config)
      response <- OptionService.issueRequest(connection,"payload")
    } yield response
    println(responseOption)
  }

  // TODO implementation of HttpService for ErrorOr[T]

  object ErrorOrHttpService extends HttpService[ErrorOr] {
    override def getConnection(cfg: Map[String, String]): ErrorOr[Connection] =
      if(!cfg.contains("host") || !cfg.contains("port"))
        Left(new RuntimeException("Connection cannot be established: invalid confgiuration"))
      else Right(Connection(cfg("host"),cfg("port")))
    override def issueRequest(connection: Connection, payload: String): ErrorOr[String] = {
      if(payload.length > 20) Left(new RuntimeException("Payload is too large"))
      else Right(s"Payload ($payload) was accepted")
    }
  }

  val responseErrorOr: ErrorOr[String] = for{
    conn <- ErrorOrHttpService.getConnection(config)
    response <- ErrorOrHttpService.issueRequest(conn,"assaas")
  } yield response

}
