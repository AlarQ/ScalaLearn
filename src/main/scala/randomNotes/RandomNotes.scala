package randomNotes

import java.io.{BufferedReader, FileReader}
import scala.util.Using

object RandomNotes extends App {

  // ---------------------------------------------------------

  // opearators ending  with ':' are linked to the wtite side
  val list = 1 :: 2 :: 3 :: Nil
  val theSamelist = (1 :: (2 :: (3 :: Nil)))
  println(list == theSamelist) // true

  // ---------------------------------------------------------

  // more optimal, get direct fields, not generated getters / setters
  private[this] val x = 12

  // ---------------------------------------------------------

  // upply and update

  class A(a: Int)

  object A {
    def apply(a: Int): A = new A(a)

    def update(a: Int, value: String) = ???

  }


  // ---------------------------------------------------------
  //  abstract override - AbstractOverride.scala
  // ---------------------------------------------------------

  // delete all comments
  // -> multiline: /\*(\*)?(((?!\*/)[\s\S])+)?\*/
  // -> oneline: //.*$

  // ---------------------------------------------------------

  // underscore usage
  def foo(x: Int) = x + 1

  // println function, not evaluation
  println(foo _)

  // ---------------------------------------------------------
  Using.resource(new BufferedReader(new FileReader("example.txt"))) { reader =>
    Iterator.continually(reader.readLine().foreach(println))
  }
  // ---------------------------------------------------------
  // views - link operations into one
  val xs = List(1, 2, 3)
    .view
    .map(_ + 1)
    .filter(_ % 2 == 0)
    .map(_ * 2)
    .flatMap(x => List(x, x))
    .toList

  // ---------------------------------------------------------
  // TODO  Either
  // ---------------------------------------------------------
  // IMPLICITS
  // -> conversions - Conversions.scala
  // -> monoid implicits - SummatorExercise.scala
  // -> implicit class - EnrichedClassExercise.scala
  // ---------------------------------------------------------
  // type class
  // Equality.scala
  // ---------------------------------------------------------
  // ClassTag
  // ---------------------------------------------------------
  // Threads
  // SemaphoreExercise.scala
  /**
   * W razie konieczności na Future można poczekać blokując wątek:
   *
   * val h = Future.successful(2)
   * val hValue: Int = Await.result(h, Duration.Inf)
   */
  // ---------------------------------------------------------


}
