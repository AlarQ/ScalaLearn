package fp_programming_in_scala_book.HandlingErrorsWithoutExceptions

object Exercise4_6 extends  App{
  sealed trait Either[+E, +A]
  case class Left[+E](value: E) extends Either[E, Nothing]
  case class Right[+A](value: A) extends Either[Nothing, A]

}
