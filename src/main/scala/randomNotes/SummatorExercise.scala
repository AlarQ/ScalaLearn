package randomNotes

object SummatorExercise {

  def sumInts(l: List[Int]): Int = l.sum

  def sum[A](l: List[A])(implicit m: Monoid[A]): A = {
    l.foldLeft(m.zero)(m.combine)
  }

  trait Monoid[A] {
    def zero: A

    def combine(a: A, b: A): A
  }

  object Monoid {
    def instance[A](z: A)(f: (A, A) => A): Monoid[A] = new Monoid[A] {
      override def zero: A = z

      override def combine(a: A, b: A): A = f(a, b)
    }

    implicit val intMonoid: Monoid[Int] = Monoid.instance(0)(_ + _)

    implicit val stringMonoid: Monoid[String] = Monoid.instance("")(_ ++ _)
    implicit val booleanMonoid: Monoid[Boolean] = Monoid.instance(true)(_ && _)


    implicit def setMonoid[A]: Monoid[Set[A]] = Monoid.instance(Set.empty[A])(_ ++ _)


    implicit def optionMonoid[A](implicit a: Monoid[A]): Monoid[Option[A]] =
      Monoid.instance(Option.empty[A]) {
        case (Some(x), Some(y)) => Option(a.combine(x, y))
        case (x, y) => x.orElse(y)
      }
  }

  def main(args: Array[String]): Unit = {

    val intList = List(1, 2, 3, 4)
    val stringList = List("a", "b", "c")
    val booleanList = List(false, true, false)
    val setList = List(Set(1, 2), Set(3, 4))
    val optionList = List(None, Option(23), Option(42))

    println(sum(intList))
    println(sum(stringList))
    println(sum(booleanList))
    println(sum(setList))
    println(sum(optionList))

    locally {

      implicit val booleanMonoid: Monoid[Boolean] = Monoid.instance(false)(_ || _)

      println(sum(booleanList))
    }
  }
}
