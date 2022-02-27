package rock_the_JVM.cats_effect.part2effects

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.util.Try

object IOErrorHandling_24 {
  // IO: pure, delay, defer
  // create failed effects
  val failedCompute: IO[Int] = IO.delay(throw new RuntimeException("failure"))
  val failure: IO[Int] = IO.raiseError(new NoSuchElementException("proper fail"))

  // handle exceptions
  val dealWithIt = failure.handleErrorWith {
    case _: RuntimeException => IO.delay(println("I'm still here!"))
    // add more cases
  }

  // turn into an Either - transform to IO[Either[]]
  val effectAsEither: IO[Either[Throwable, Int]] = failure.attempt
  // redeem: transform success and failure at the same tome

  val resultAsString = failure.redeem(ex => s"FAIL: $ex", value => s"Success: $value")

  // redeemWith - like flatMap
  val resultAsEffect: IO[Unit] = failure.redeemWith(ex => IO(println(s"FAIL: $ex")), value => IO(s"Success: $value"))

  /**
   * Exercises:
   * 1 - construct potentially failed IOs: Option, Try, Either
   * 2 - handleError, handleErrorWith
   */

  // 1 - look in implementation of IO methods
  def optionToIO[A](option: Option[A])(ifEmpty: Throwable): IO[A] = IO.fromOption(option)(ifEmpty)

  def tryToIO[A](tryEx: Try[A])(ifEmpty: Throwable): IO[A] = IO.fromTry(tryEx)

  def eitherToIO[A](either: Either[Throwable, A]): IO[A] = IO.fromEither(either)

  // 2
  def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] = io.redeem(handler,identity)

  def handleIOErrorWith[A](io: IO[A])(handler: Throwable => IO[A]): IO[A] = io.redeemWith(handler,IO.pure)

  def main(args: Array[String]): Unit = {
    println(resultAsString.unsafeRunSync())


  }
}
