package rock_the_JVM.cats.chapter_2

import cats.Monad
import cats.implicits.catsSyntaxApplicativeId

object Monads_24 {

  val numbers = List(1, 2, 3)
  val charList = List("a", "b", "c")

  /**
   * This also can be done using for-comprehensions
   * which are equivalent to this solution/
   * Similar solution can be obtain for options, futures etc.
   */
  val combinations = numbers.flatMap(numb => charList.map((numb, _)))

  /**
   * Pattern:
   * - wrapping a value into M value
   * - flatMap mechanism
   *
   */

  trait MyMonad[M[_]] {
    def pure[A](value: A): M[A]

    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

    // in cat also it is implemented,so it clearly extends Functor
    def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(x => pure(f(x)))
  }

  val optionMonad = Monad[Option]
  val option = optionMonad.pure(4) // Option(4) == Some(4)
  val transformOption = optionMonad.flatMap(option)(x => if (x % 3 == 0) Some(x + 1) else None)

  // specialized API - works only for List, what about Option, Future etc.? How do generalize it?
  def getPairsList(numbers: List[Int], chars: List[Char]): List[(Int, Char)] = numbers.flatMap(n => chars.map((n, _)))

  // generalized API
  def getPairs[M[_], A, B](ma: M[A], mb: M[B])(implicit monad: Monad[M]): M[(A, B)] =
    monad.flatMap(ma)(a => monad.map(mb)(b => (a, b)))

  // extension methods - pure, flatMap
  val oneOption = 1.pure[Option] // Some(1)
  val oneList = 1.pure[List]

  // here flatMap is used from Option.scala, but it will be useful for types,
  // which don't have this method
  val oneOptionTransformed = oneOption.flatMap(x => (x + 1).pure[Option])
}
