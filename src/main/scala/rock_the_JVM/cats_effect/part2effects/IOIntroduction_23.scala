package rock_the_JVM.cats_effect.part2effects

import cats.effect.IO

import scala.io.StdIn

object IOIntroduction_23 {

  val ourFirstIO: IO[Int] = IO.pure(42)
  val aDelayedIO: IO[Int] = IO.delay({
    println("Producing intiger")
    54
  })

  val aDelayedIO_v2: IO[Int] = IO({ // apply == delay
    println("Producing intiger")
    54
  })

  // pure used only for arguments with NO side effects!
  val shouldNotDoThis: IO[Int] = IO.pure({
    println("Producing intiger")
    54
  })

  val improvedOurFirstIO = ourFirstIO.map(_ * 2)
  val printedOurFirstIO = ourFirstIO.flatMap(x => IO(println(x)))

  def smallProgram(): IO[Unit] = for {
    line1 <- IO(StdIn.readLine())
    line2 <- IO(StdIn.readLine())
    _ <- IO(println(line1 + line2))
  } yield ()

  // mapN - combine IO effects as tuples

  import cats.syntax.apply._

  val combinedIO = (ourFirstIO, improvedOurFirstIO).mapN(_ + _)

  def smallProgram_v2(): IO[Unit] =
    (IO(StdIn.readLine()), IO(StdIn.readLine())).mapN(_ + _).map(println)

  /**
   * Ecercises:
   * 1 - sequence two IOs and take the result of last one
   * 2 - sequence two IOs and take the result of first one
   * 3 - repeat IO effect forever
   * 4 - convert IO to different type
   * 5 - discard value inside IO, just return Unit
   * 6 - fix stack recursion
   * 7 - write fib with IO, does not crashon recursion
   */
  // 1
  def sequenceTakeLast[A, B](ioa: IO[A], iob: IO[B]): IO[B] = for {
    _ <- ioa
    io2 <- iob
  } yield io2

  def sequenceTakeLast_v2[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
    ioa *> iob // andThen

  def sequenceTakeLast_v3[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
    ioa >> iob // andThen with by name call

  // 2
  def sequenceTakeFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] = for {
    io1 <- ioa
    _ <- iob
  } yield io1

  def sequenceTakeFirst_v2[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
    ioa.flatMap(a => iob.map(_ => a))

  def sequenceTakeFirst_v3[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
    ioa <* iob

  // 3
  def forever[A](io: IO[A]): IO[Unit] =
    io.flatMap(_ => forever(io))

  def forever_v2[A](io: IO[A]): IO[Unit] =
    io >> forever_v2(io)

  // stackOverFlow
  def forever_v3[A](io: IO[A]): IO[Unit] =
    io *> forever_v3(io)

  def forever_v4[A](io: IO[A]): IO[Unit] =
    io.foreverM

  // 4
  def convert[A, B](ioa: IO[A], value: B): IO[B] =
    ioa.map(_ => value)

  def convert_v2[A, B](ioa: IO[A], value: B): IO[B] =
    ioa.as(value)

  // 5
  def asUnit[A](io: IO[A]): IO[Unit] = convert(io, ())

  def asUnit_v2[A](io: IO[A]): IO[Unit] = io.map(_ => ())

  def asUnit_v3[A](io: IO[A]): IO[Unit] = io.as(()) // unreadable! - don't use this

  def asUnit_v4[A](io: IO[A]): IO[Unit] = io.void

  // 6
  def sum(n: Int): Int =
    if (n <= 0) 0
    else n + sum(n - 1)

  // 7
  def fibonacci(n: Int): IO[BigInt] =
    if(n<2) IO(1)
    else for {
      last <- IO(fibonacci(n-1)).flatten // flatMap(x => x)
      prev <- IO.defer(fibonacci(n-2))// IO(fibonacci(n-2)).flatMap(x => x)
    } yield last + prev

  // uses flatMap -> stack-safe
  def sumIO(n: Int): IO[Int] =
    if(n <=0) IO(0)
    else for{
      lastNumber <- IO(n)
      prevSum <- sumIO(n -1)
    } yield prevSum + lastNumber

  def main(args: Array[String]): Unit = {
    // unsafeRunAsync takes implicit IORuntime - thread pool with additional functionalities
    import cats.effect.unsafe.implicits.global
    //    smallProgram().unsafeRunSync()

//    forever_v3(IO(println("forever!"))).unsafeRunSync()
    println(fibonacci(10).unsafeRunSync())
  }
}
