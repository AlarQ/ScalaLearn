package randomNotes.parconc

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * == TODO 1 ==
 * Uzyj metod na obiekcie Future, aby zaimplementować metodę sumFutures.
 *
 * == TODO 2 ==
 * Użyj pętli FOR/YIELD aby zaimplementować metodę sumFuturesFor.
 *
 * == TODO 3 ==
 * Uruchom testy w FutureExercisesSuite
 */
object FutureExercises {
  def sumFutures(f1: Future[Int], f2: Future[Int], f3: Future[Int]): Future[Int] = {
    f1.zipWith(f2)(_ + _).zipWith(f3)(_ + _)
  }

  def sumFuturesFor(f1: Future[Int], f2: Future[Int], f3: Future[Int]): Future[Int] = {
    for {
      a <- f1
      b <- f2
      c <- f3
    } yield (a + b + c)
  }
}
