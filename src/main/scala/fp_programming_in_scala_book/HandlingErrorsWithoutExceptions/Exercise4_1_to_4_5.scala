package fp_programming_in_scala_book.HandlingErrorsWithoutExceptions

object Exercise4_1_to_4_5 extends App {

  trait Option[+A] {
    def map[B](f: A => B): Option[B] = this match {
      case Some(x) => Some(f(x))
      case None => None
    }

    def flatMap[B](f: A => Option[B]): Option[B] = this match {
      case Some(x) => f(x)
      case None => None
    }

    def flatMap_1[B](f: A => Option[B]): Option[B] =
      map(f) getOrElse None

    def getOrElse[B >: A](default: => B): B = this match {
      case Some(x) => x
      case None => default
    }

    def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
      case Some(x) => this
      case None => ob
    }

    def orElse_1[B >: A](ob: => Option[B]): Option[B] =
      this map (Some(_)) getOrElse ob


    def filter(f: A => Boolean): Option[A] = this match {
      case Some(x) if f(x) => this
      case _ => None
    }

    def filter_1(f: A => Boolean): Option[A] =
      flatMap(a => if (f(a)) Some(a) else None)

  }

  case class Some[+A](get: A) extends Option[A]

  case object None extends Option[Nothing]

  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)

  def variance(xs: Seq[Double]): Option[Double] =
    mean(xs) flatMap (m => mean(xs.map(x => math.pow(x - m, 2))))

  def Try[A](a: => A): Option[A] =
    try Some(a)
    catch {
      case e: Exception => None
    }

  /**
   * Write a generic function map2 that combines two Option values using a binary function.
   * If either Option value is None, then the return value is too.
   * Here is its signature:
   */
  def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = (a, b) match {
    case (Some(x), Some(y)) => Some(f(x, y))
    case _ => None
  }

  def map2a[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] =
    a flatMap (aa => b map (bb => f(aa, bb)))

  /**
   * Write a function sequence that combines a list of Options into one Option containing
   * a list of all the Some values in the original list. If the original list contains None even
   * once, the result of the function should be None; otherwise the result should be Some
   * with a list of all the values.
   */
  def sequence[A](a: List[Option[A]]): Option[List[A]] =
    a match {
      case Nil => Some(Nil)
      case h :: t => h flatMap (hh => sequence(t) map (hh :: _))
    }

  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
    a match {
      case Nil => Some(Nil)
      case h::t => map2(f(h), traverse(t)(f))(_ :: _)
    }

  def traverse_1[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
    a.foldRight[Option[List[B]]](Some(Nil))((h,t) => map2(f(h),t)(_ :: _))

  def sequenceViaTraverse[A](a: List[Option[A]]): Option[List[A]] =
    traverse(a)(x => x)
}
