package rock_the_JVM.cats.chapter_1

import cats.Eq
import cats.implicits.catsSyntaxEq

object CatsIntro_14 extends App {

  /**
   * Here we have important imports needed when using cats
   * I'm not sure if they are still needed in newer versions of cats
   */
  // wrong - will trigger compiler warning
  val aComparison = 2 == "a string"
  println(aComparison)
  // #1 - typeClass import
  //import cats.Eq

  // #2 - import TC instances fo type you need

  // #3 - use TC API
  val intEquality: Eq[Int] = Eq[Int]
  val aTypeSafeComparison = intEquality.eqv(2, 3) // false
  //  val anUnsafeComparison = intEquality.eqv(2,"ssss") // doesn't compile

  // #4 - use extension method (if applicable)

  val anotherTypeSafeComp = 2 === 3 // false
  val neqComparison = 2 =!= 3 // true
  // whole point of typeclasess
  //  val invalidComparison = 2 === "a string" // not compile

  // #5 - extending the TC operations to composite type
  val aListComparison = List(2) === List(2)
  println(aListComparison)

  // #6 - create type class instance for a custom type
  case class ToyCar(model: String, price: Double)

  implicit val toyCarEq: Eq[ToyCar] = Eq.instance[ToyCar] { (car1, car2) =>
    car1.price == car2.price
  }


}
