package rock_the_JVM.cats

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

  // TODO exercises



  def main(args: Array[String]): Unit = {

  }

}
