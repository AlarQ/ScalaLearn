package rock_the_JVM.cats.chapter_2

import cats.data.{EitherT, OptionT}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

object MonadTransformers_28 {

  // # - Option transformer
  // to implement this you have to unwrap every single object inside values list
  // and wrap it back
  def sumAllOptions(values: List[Option[Int]]): Int = ???

//   Monad transformers help us to work with nested monads
//   without need of unwrapping it many times

  // list of options of int
  val listOfNumberOptions: OptionT[List, Int] = OptionT(List(Option(1), Option(2)))
  val listOfCharOptions: OptionT[List, Char] = OptionT(List(Option('a'), Option('d')))
  val listOfTuples: OptionT[List, (Int, Char)] = for {
    char <- listOfCharOptions
    number <- listOfNumberOptions
  } yield (number, char)

  println(listOfTuples.value)

  val listOfEither: EitherT[List, String, Int] = EitherT(List(Left("something wrong"), Right(23)))
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))
  val futureOfEither: EitherT[Future, String, Int] = EitherT(Future[Either[String,Int]](Right(23)))
//  val futureOfEither2: EitherT[Future, String, Int] = EitherT.right(23)

  /**
   * Exercises:
   * We have multi-machine cluster for your business, which will receive a traffic surge following a media appearance.
   * We measure bandwidth in units
   * We want to allocate 2 of our servers to cope with the traffic spike
   * We know the current capacity for each server and we will hold the traffic if the sum of bandwidths is > 250
   */
  val bandwidths = Map(
    "server 1" -> 50,
    "server 2" -> 300,
    "server 3" -> 170
  )

  type AsyncResponse[T] = EitherT[Future, String, T]

  def getBandwidth(server: String): AsyncResponse[Int] = bandwidths.get(server) match {
    case None => EitherT(Future[Either[String,Int]](Left("Server unreachable")))
    case Some(b) => EitherT(Future[Either[String,Int]](Right(b)))
  }

  def canWithstandSurge(s1: String, s2: String): AsyncResponse[Boolean] =
    for {
      b1 <- getBandwidth(s1)
      b2 <- getBandwidth(s2)
      answer = b1 + b2 > 250
    } yield answer

  def generateTrafficSpikeReport(s1: String, s2: String): AsyncResponse[String] =
    canWithstandSurge(s1, s2).transform {
      case Left(reason) => Left(s"Server are not good: $reason")
      case Right(false) => Left(s"Not enough total bandwidth")
      case Right(true) => Right("Servers are good")
    }



  def main(args: Array[String]): Unit = {
    println(listOfTuples.value)
  }
}
