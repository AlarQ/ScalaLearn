package randomNotes.parconc

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.async.Async.{async, await}

/**
 * == TODO 1 ==
 * Rozszerz Future[T] o metodę exists[Suffix]:
 *
 * {{{
 *   def exists[Suffix](f: T => Boolean): Future[Boolean]
 * }}}
 *
 * Metoda ta zwraca Future(true) jeśli wartość wyliczona w źródłowym future
 * spełnia podany warunek.
 *
 * Przykład użycia:
 * {{{
 *   val f: Future[Boolean] = Future(2).exists(_ % 2 == 0)
 *   // oblicza się do true
 * }}}
 *
 * Metodę napisz na trzy sposoby:
 *  - existsComb - skorzystaj z kombinatorów na Future, nie używaj Promise/Async
 *  - existsProm - skorzystaj z Promise
 *  - existsAsync - skorzystaj z Async
 *
 *  == TODO 2 ==
 *  Przetestuj implementacje za pomocą FutureExercises2Suite.
 */
object FutureExercises2 {

  implicit class FutureOps[A](f: Future[A]) {
    // tutaj umieść metody existsComb/existsProm/existsAsync
    def existsComb(fun: A => Boolean)(implicit ec: ExecutionContext): Future[Boolean] =
      f.map(fun)

    def existsProm(fun: A => Boolean)(implicit ec: ExecutionContext): Future[Boolean] = {
      val p: Promise[Boolean] = Promise[Boolean]
      f.onComplete {
        result =>
          p.tryComplete(result.map(fun))
      }
      p.future
    }

    def existsAsync(fun: A => Boolean)(implicit ec: ExecutionContext): Future[Boolean] = {
      async {
        val result: A = await(f)
        fun(result)
      }
    }
  }
}
