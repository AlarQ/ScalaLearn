import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

object CallByName {

  def byValueFunction(x: Int) = x + 12

  // 2 + 3 is evaluated first
  byValueFunction(2 + 3)

  def byNameFunction(x: => Int) = x + 12

  // 2 + 3 is passed LITERALLY, evaluated, when used first
  byNameFunction(2 + 3)

  // -----------------------------------------------------
  // trick #1 - reevaluation

  def byValuePrint(x: Long) = {
    println(x)
    println(x)
  }

  def byNamePrint(x: => Long) = {
    println(x)
    println(x)
  }

  def main(args: Array[String]): Unit = {
    println("Two times the same value: called by value")
    byValuePrint(System.nanoTime())
    println("Different values: called by name")
    byNamePrint(System.nanoTime())
  }

  // -----------------------------------------------------
  // trick #2 - call by need, powerful in infinite collections, used in LazyList
  abstract class MyList[+T] {
    def head: T

    def tail: MyList[T]
  }

  class NonEmptyList[+T](h: => T, t: => MyList[T]) extends MyList[T] {
    override lazy val head: T = h
    override lazy val tail: MyList[T] = t
  }

  // -----------------------------------------------------
  // trick #3 - hold the door

  // Try.apply must have byName parameters, cause of exceptions
  //
  val anAttempt: Try[Int] = Try {
    throw new NullPointerException
  }

  // -----------------------------------------------------

}
