package rock_the_JVM.advanced_scala.Implicits

object ImplicitsIntro extends App {

  // why this compiles?
  val pair = "Adam" -> "222"
  val intPair = 12 -> 23

  // ------------------------ IMPLICIT CONVERSION --------------------------
  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)
  // compiler look for a implicit class or method which can wrap the string and call method greet
  println("Peter".greet) // rewritten to println(fromStringToPerson("Peter").greet)

  // compiler assumes that it is ONLY ONE implicit that matches
  //  class A{
  //    def greet: Int = 2
  //  }
  //  implicit def fromStringToA(str: String) : A = new A

  // -------------------------- IMPLICIT PARAMETERS ------------------------------
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10
  increment(2)

}
