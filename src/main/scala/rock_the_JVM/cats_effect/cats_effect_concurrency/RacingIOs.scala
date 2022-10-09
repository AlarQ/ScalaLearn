package rock_the_JVM.cats_effect.cats_effect_concurrency

import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{FiberIO, IO, IOApp, OutcomeIO}
import rock_the_JVM.cats_effect.utils.DebugWrapper

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

object RacingIOs extends IOApp.Simple {

  def runWithSleep[A](value: A, duration: FiniteDuration): IO[A] =
    IO(s"starting computation $value").debug >>
      IO.sleep(duration) >>
      IO(s"computation $value done") >>
      IO(value)
        .onCancel(IO(s"computation CANCELED for $value: done").debug.void)

  /**
   * We can run two separate effects on two separate fibers
   * and race them to obtain only on of their result
   * result of which finishes raceResult
   */

  def testRace() = {
    val meaningOfLife = runWithSleep(42, 1 second)
    val favLang = runWithSleep("Scala", 2 seconds)
    /**
     * IO.race:
     * - both IOs run on separate fibers
     * - the raceResult one to finish will complete the result
     * - the loser will be cancelled
     */
    val first: IO[Either[Int, String]] = IO.race(meaningOfLife, favLang)
    first.flatMap {
      case Left(value) => IO(s"Meaning of life: $value")
      case Right(value) => IO(s"Favorite language $value")
    }
  }

  /**
   * more general version of race:
   * - allows to handle value pf loser effect
   * - so you can cancel it if you want
   * - it isn't canceled automatically
   */
  def testRacePair() = {
    val meaningOfLife = runWithSleep(42, 1 second)
    val favLang = runWithSleep("Scala", 2 seconds)
    // TODO EB - understand type!
    val raceResult: IO[Either[(OutcomeIO[Int], FiberIO[String]), (FiberIO[Int], OutcomeIO[String])]] = IO.racePair(meaningOfLife, favLang)
    raceResult.flatMap {
      case Left((outMol, fibFavLang)) => fibFavLang.cancel >> IO(s"WON: Meaning of life: $outMol").debug
      case Right((fibOfMol, outFavLang)) => fibOfMol.cancel >> IO(s"WON: Favorite language: $outFavLang").debug
    }
  }

  /** Exxercises:
   * 1 - implement a timeout pattern with race
   * 2 - a method to return a losing effect from race
   * 3 - implement race in terms of race pair
   */


  // #1
  def timeout[A](io: IO[A], duration: FiniteDuration): IO[A] = {
    val result = IO.race(io, IO.sleep(duration))
    result.flatMap {
      case Left(value) => IO(value)
      case Right(_) => IO.raiseError(new RuntimeException("computation time out"))
    }
  }

  // #2
//  def urnace[A, B](ioa: IO[A], iob: IO[B]): IO[Either[A, B]] = {
//    val raceResult = IO.racePair(ioa, iob)
//    raceResult.flatMap {
//      case Left((_, fibB)) => fibB.join.flatMap {
//        case Succeeded(resultEffect) => resultEffect.map(Right(_))
//        case Errored(e) => IO.raiseError(e)
//        case Canceled() => IO.raiseError(new RuntimeException("Loser canceled"))
//      }
//      case Right((fibA, _)) => fibA.join.flatMap {
//        case Succeeded(resultEffect) => resultEffect.map(Right(_))
//        case Errored(e) => IO.raiseError(e)
//        case Canceled() => IO.raiseError(new RuntimeException("Loser canceled"))
//      }
//    }}

    // #3
    def simpleRace[A, B](ioa: IO[A], iob: IO[B]): IO[Either[A, B]] =
      IO.racePair(ioa, iob).flatMap {
        case Left((outA, fibB)) => outA match {
          case Succeeded(effectA) => fibB.cancel >> effectA.map(a => Left(a))
          case Errored(e) => fibB.cancel >> IO.raiseError(e)
          case Canceled() => fibB.join.flatMap {
            case Succeeded(effectB) => effectB.map(b => Right(b))
            case Errored(e) => IO.raiseError(e)
            case Canceled() => IO.raiseError(new RuntimeException("Both computations canceled."))
          }
        }
        case Right((fibA, outB)) => outB match {
          case Succeeded(effectB) => fibA.cancel >> effectB.map(b => Right(b))
          case Errored(e) => fibA.cancel >> IO.raiseError(e)
          case Canceled() => fibA.join.flatMap {
            case Succeeded(effectA) => effectA.map(a => Left(a))
            case Errored(e) => IO.raiseError(e)
            case Canceled() => IO.raiseError(new RuntimeException("Both computations canceled."))
          }
        }
      }


    val importantTask = IO.sleep(2 seconds) >> IO(42).debug
    val testTimeout = timeout(importantTask, 1 second)
    // we can use cats effect
    val testTimeout_V2 = importantTask.timeout(1 second)

    override def run: IO[Unit] = testTimeout.debug.void
  }
