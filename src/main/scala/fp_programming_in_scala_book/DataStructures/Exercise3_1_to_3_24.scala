package fp_programming_in_scala_book.DataStructures

import scala.annotation.tailrec

object Exercise3_1_to_3_24 extends App {

  sealed trait List[+A]

  case object Nil extends List[Nothing]

  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {

    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(x, xs) => x + sum(xs)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
      case Cons(x, xs) => x * product(xs)
    }

    /**
     * Implement the function tail for removing the first element of a List
     *
     * Although we could return `Nil` when the input list is empty, we choose to throw an exception instead. This is
     * a somewhat subjective choice. In our experience, taking the tail of an empty list is often a bug, and silently
     * returning a value just means this bug will be discovered later, further from the place where it was introduced.
     */
    def tail[A](xs: List[A]): List[A] = xs match {
      case Nil => sys.error("Tail of empty list")
      case Cons(_, ys) => ys
    }

    /**
     * implement the function setHead for replacing the first element
     * of a List with a different value
     *
     */
    def setHead[A](xs: List[A], value: A): List[A] = xs match {
      case Nil => Cons(value, Nil)
      case Cons(_, ys) => Cons(value, ys)
    }

    /**
     * Generalize tail to the function drop, which removes the first n elements from a list
     */
    def drop[A](l: List[A], n: Int): List[A] = n match {
      case 0 => l
      case 1 => tail(l)
      case n if n > 1 => drop(tail(l), n - 1)
    }

    def drop1[A](l: List[A], n: Int): List[A] =
      if (n <= 0) l
      else l match {
        case Nil => Nil
        case Cons(_, t) => drop(t, n - 1)
      }

    /**
     * Implement dropWhile, which removes elements from the List prefix as long as they
     * match a predicate.
     */
    @tailrec
    def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Cons(h, t) if f(h) => dropWhile(t, f)
      case _ => l
    }

    /**
     * Implement a function, init, that returns a List
     * consisting of all but the last element of a List. So, given List(1,2,3,4), init will
     * return List(1,2,3).
     */
    def init[A](l: List[A]): List[A] = l match {
      case Nil => sys.error("init of empty list")
      case Cons(_, Nil) => Nil
      case Cons(h, t) => Cons(h, init(t))
    }

    def init2[A](l: List[A]): List[A] = {
      import collection.mutable.ListBuffer
      val buf = new ListBuffer[A]

      @annotation.tailrec
      def go(cur: List[A]): List[A] = cur match {
        case Nil => sys.error("init of empty list")
        case Cons(_, Nil) => List(buf.toList: _*)
        case Cons(h, t) => buf += h; go(t)
      }

      go(l)
    }

    // TODO I AM HERE!


    def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B =
      as match {
        case Nil => z
        case Cons(x, xs) => f(x, foldRight(xs, z)(f))
      }

    /**
     * Compute the length of a list using foldRight.
     */
    def length[A](as: List[A]): Int = foldRight(as, 0)((_, acc) => acc + 1)


    def sum2(ns: List[Int]) =
      foldRight(ns, 0)((x, y) => x + y)

    def product2(ns: List[Double]) =
      foldRight(ns, 1.0)(_ * _)

    /**
     * writhe foldLeft function
     */
    @annotation.tailrec
    def foldLeft[A, B](l: List[A], z: B)(f: (B, A) => B): B = l match {
      case Nil => z
      case Cons(h, t) => foldLeft(t, f(z, h))(f)
    }

    def sum3(l: List[Int]) = foldLeft(l, 0)(_ + _)

    def product3(l: List[Double]) = foldLeft(l, 1.0)(_ * _)

    def length2[A](l: List[A]): Int = foldLeft(l, 0)((acc, _) => acc + 1)

    /**
     * write reverse function using fold
     */
    def reverse[A](l: List[A]): List[A] =
      foldLeft(l, List[A]())((acc, h) => Cons(h, acc))

    /**
     *
     * Write a function that transforms a list of integers by adding 1 to each element.
     * (Reminder: this should be a pure function that returns a new List!)
     */
    def addOne(as: List[Int]): List[Int] = as match {
      case Nil => Nil
      case Cons(h, t) => Cons(h + 1, addOne(t))
    }

    def add1(l: List[Int]): List[Int] =
      foldRight(l, Nil: List[Int])((h, t) => Cons(h + 1, t))

    /**
     * Write a function that turns each value in a List[Double] into a String. You can use
     * the expression d.toString to convert some d: Double to a String.
     */
    def doubleToString(l: List[Double]): List[String] =
      foldRight(l, Nil: List[String])((h, t) => Cons(h.toString, t))

    /**
     * Write a function map that generalizes modifying each element in a list while maintaining the structure of the list.*
     */
    def map[A, B](as: List[A])(f: A => B): List[B] = foldRight(as, Nil: List[B])((h, t) => Cons(f(h), t))

    def foldRightViaFoldLeft[A, B](l: List[A], z: B)(f: (A, B) => B): B =
      foldLeft(reverse(l), z)((b, a) => f(a, b))

    //stack-safe
    def map_1[A, B](l: List[A])(f: A => B): List[B] =
      foldRightViaFoldLeft(l, Nil: List[B])((h, t) => Cons(f(h), t))


    def filter[A](l: List[A])(f: A => Boolean): List[A] =
      foldRight(l, Nil: List[A])((h, t) => if (f(h)) Cons(h, t) else t)

    // stack-safe
    def filter_1[A](l: List[A])(f: A => Boolean): List[A] =
      foldRightViaFoldLeft(l, Nil: List[A])((h, t) => if (f(h)) Cons(h, t) else t)

    def append[A](a1: List[A], a2: List[A]): List[A] =
      a1 match {
        case Nil => a2
        case Cons(h, t) => Cons(h, append(t, a2))
      }

    def concat[A](l: List[List[A]]): List[A] =
      foldRight(l, Nil: List[A])(append)

    /**
     * Write a function flatMap that works like map except that the function given will return
     * a list instead of a single result, and that list should be inserted into the final resulting list
     */
    def flatMap[A, B](as: List[A])(f: A => List[B]): List[B] = concat(map(as)(f))

    /**
     * Write a function that accepts two lists and constructs a new list by adding corresponding elements
     */
    def addPairwise(a: List[Int], b: List[Int]): List[Int] = (a, b) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(h1 + h2, addPairwise(t1, t2))
    }

    def zipWith[A, B, C](a: List[A], b: List[B])(f: (A, B) => C): List[C] = (a, b) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(t1, t2)(f))
    }

    def apply[A](as: A*): List[A] =
      if (as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))
  }

  /**
   * what it returns?
   * answer: 3
   */
  val xs = List(1, 2, 3, 4, 5)
  val x = xs match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + List.sum(t)
    case _ => 101
  }

  println(List.addOne(xs))
}
