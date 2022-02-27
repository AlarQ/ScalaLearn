package rock_the_JVM.cats_effect.part2effects

import cats.Parallel
import cats.effect.implicits.commutativeApplicativeForParallelF
import cats.effect.{IO, IOApp, ParallelF}
import rock_the_JVM.cats_effect.utils.DebugWrapper

object IOParallelism_27 extends IOApp.Simple {

  // IOs are usually sequential
  val sampleIO1 = IO(s"[${Thread.currentThread().getName}] first")
  val sampleIO2 = IO(s"[${Thread.currentThread().getName}] second")

  val composedIO = for{
    first <- sampleIO1
    second <- sampleIO2
  } yield s"$first and $second"
  val meaningOfLife: IO[Int] = IO(42)
  val favLang: IO[String] = IO("Scala")

  import cats.syntax.apply._
  val goalInLife =(meaningOfLife,favLang).mapN((num, str) => s"my goal in life is $num and $str")

  // parallelism of IOs
  // convert a sequential IO to parallel IO
  val parIO_1: IO.Par[Int] = Parallel[IO].parallel(meaningOfLife.debug)
  val parIO_2: IO.Par[String] = Parallel[IO].parallel(favLang.debug)
val goalInLifePar =(parIO_1,parIO_2).mapN((num, str) => s"my goal in life is $num and $str")

  // turn back to sequential
  val goalInLife_v2 = Parallel[IO].sequential(goalInLifePar)

  // shorthand
  import cats.syntax.parallel._
  val goalInLife_v3: IO[String] = (meaningOfLife.debug,favLang.debug).parMapN((num, str) => s"my goal in life is $num and $str")


  // regarding failure
  val failure: IO[String] = IO.raiseError(new RuntimeException("I can't do this"))
  // compose success and failure
  val parallelWithFailure = (meaningOfLife.debug,failure.debug).parMapN(_ + _)

  // compose two failures
  val anotherFailure: IO[String] = IO.raiseError(new RuntimeException("Second failure"))
  val parallelWithTwoFailures = (failure.debug,anotherFailure.debug).parMapN(_ + _)
  // failure of entire effect is a failure of FIRST one failed
  override def run: IO[Unit] = parallelWithTwoFailures.debug.void
}
