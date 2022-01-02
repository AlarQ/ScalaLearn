package rock_the_JVM.cats

import cats.Monad

import scala.annotation.tailrec

object CustomMonads_27 {
  // define our own monad instance
  implicit object optionMonad extends Monad[Option]{
    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)
    override def pure[A](x: A): Option[A] = Option(x)

    // needed for some iterations monad.iter...
    @tailrec
    override def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] = f(a) match {
      case None => None
      case Some(Left(a)) => tailRecM(a)(f)
      case Some(Right(b)) => Some(b)
    }
  }
}
