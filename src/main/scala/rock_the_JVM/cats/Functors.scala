package rock_the_JVM.cats

import cats.Functor
import cats.implicits.toFunctorOps

object Functors {

  // simplified definition -
  trait MyFunctor[F[_]] {
    def map[A, B](initialValue: F[A])(f: A => B): F[B]
  }

  val listFunctor = Functor[List]

  def incrementedNumbers = listFunctor.map(List(1, 2, 3))(_ + 1)
  // can do this for option, try etc.

  // generalizing an API
  def do10xList(list: List[Int]): List[Int] = list.map(_ + 10)

  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ + 10)

  def do10x[F[_]](container: F[Int])(implicit functor: Functor[F]): F[Int] = functor.map(container)(_ * 10)

  // another example
  trait Tree[+T]

  object Tree {
    def leaf[T](value: T) = Leaf(value)

    def branch[T](value: T, left: Tree[T], right: Tree[T]): Tree[T] = Branch(value, left, right)
  }

  case class Leaf[+T](value: T) extends Tree[T]

  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]

  implicit object TreeFunctor extends Functor[Tree] {
    override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
      case Leaf(value) => Leaf(f(value))
      case Branch(value, left, right) => Branch(f(value), map(left)(f), map(right)(f))
    }
  }

  // extension method - map
  val tree:Tree[Int] = Tree.branch(40,Tree.leaf(12),Tree.leaf(123))
  val incrementedTree = tree.map(_ + 1)

  def main(args: Array[String]): Unit = {
    println(do10x(List(1, 2, 3)))
    println(do10x(Option(10)))
    println(do10x(Tree.branch(30, Tree.leaf(10), Tree.leaf((20)))))
  }
}
