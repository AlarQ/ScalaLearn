package rock_the_JVM.cats.chapter_4

import cats.data.Validated
import cats.{Monad, Semigroupal}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

class Semigroupal_41 {

  trait MySemigroupal[F[_]] {
    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
  }

  val optionSemigroupal: Semigroupal[Option] = Semigroupal[Option]
  val aTupleOption: Option[(Int, String)] = optionSemigroupal.product(Some(123), Some("hello"))
  val aNoneTupled: Option[(Int, Nothing)] = optionSemigroupal.product(Some(123), None) // None

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  val aTupledFuture: Future[(String, Int)] = Semigroupal[Future].product(Future("heello"), Future(23))

  // Why Semigroupal is useful?
  val tupledList: Seq[(Int, String)] = Semigroupal[List].product(List(1, 2), List("a", "b")) // Cartesian product

  // TODO - implement
  def productWithMonads[F[_], A, B](fa: F[A], fb: F[B])(implicit monad: Monad[F]): F[(A, B)] =
    monad.flatMap(fa)(a => monad.map(fb)(b => (a, b)))

  // clearer

  import cats.syntax.functor._
  import cats.syntax.flatMap._

  def productWithMonadsCleare[F[_], A, B](fa: F[A], fb: F[B])(implicit monad: Monad[F]): F[(A, B)] = {
    for {
      a <- fa
      b <- fb
    } yield (a, b)
  }

  // We can define product using flatMap, map -> MONAD EXTEND SEMIGROUPAL
  // Why we need Semigroupal?
  // Monad laws gives us certainty that operations using flatmao, map will be done in given order
  // but sometimes we don't need that: example - Validated
  type ErrorsOr[T] = Validated[List[String], T]
  val validatedSemigroupal = Semigroupal[ErrorsOr]

  val invalidsCombination = validatedSemigroupal.product(
    Validated.invalid(List("Something wrong", "Something else wrong")),
    Validated.invalid(List("This can't be right"))
  )

  type EitherErrorsOr[T] = Either[List[String],T]
  val eitherSemigroupal = Semigroupal[EitherErrorsOr]
  val eithersCombination = eitherSemigroupal.product(
    Left(List("Something wrong", "Something else wrong")),
    Left(List("This can't be right"))
  )

  // invalidsCombination and eithersCombination gives different output
}
