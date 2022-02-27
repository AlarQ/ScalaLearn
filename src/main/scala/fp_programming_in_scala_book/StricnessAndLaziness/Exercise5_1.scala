//package fp_programming_in_scala_book.StricnessAndLaziness
//
//
//sealed trait Stream[+A] {
//  def headOption[A]: Option[A] = this match {
//    case Empty => None
//    case Cons(h, t) => Some(h())
//  }
//
//  def toList[A]: List[A] = {
//    def loop(s: Stream[A], acc: List[A]): List[A] = s match {
//      case Cons(h, t) => loop(t(), h() :: acc)
//      case _ => acc
//    }
//
//    loop(this, List()).reverse
//  }
//
//  def take(n: Int): Stream[A] = this match {
//    case Cons(h, t) if n > 1 => cons(h(), t().take(n - 1))
//    case Cons(h, _) if n == 1 => cons(h(), empty)
//    case _ => empty
//  }
//
//  @annotation.tailrec
//  final def drop(n: Int): Stream[A] = this match {
//    case Cons(_, t) if n > 0 => t().drop(n - 1)
//    case _ => this
//  }
//
//  def takeWhile(p: A => Boolean): Stream[A] = this match {
//    case Cons(h, t) if p(h()) => cons(h(), t().takeWhile(p))
//    case _ => empty
//  }
//}
//
//case object Empty extends Stream[Nothing]
//
//case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]
//
//object Stream {
//  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
//    lazy val head = hd
//    lazy val tail = tl
//    Cons(() => head, () => tail)
//  }
//
//  def empty[A]: Stream[A] = Empty
//
//  def apply[A](as: A*): Stream[A] =
//    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))
//}
//
