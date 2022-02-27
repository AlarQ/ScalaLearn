package rock_the_JVM.cats.chapter_3

import cats.Semigroup
import cats.implicits.catsSyntaxValidatedId

import scala.annotation.tailrec
import scala.util.Try

object Validated_35 {


  import cats.data.Validated

  // Act similarly to Either
  val validValue: Validated[String, Int] = Validated.valid(42)
  val invalidValue: Validated[String, Int] = Validated.invalid("Something went wrong!")
  val aTest: Validated[String, Int] = Validated.cond(42 > 32, 33, "to small")


  // Validated is used to combine all errors in one giant value using FP
  // Exercise: Todo either
  // m <= 100, n must be even, n is non-negative, n is Prime
  def testPrime(n: Int): Boolean = {

    @tailrec
    def tailrecPrime(d: Int): Boolean =
      if (d <= 1) true
      else n % d != 0 && tailrecPrime(d - 1)

    if (n == 0 || n == 1 || n == -1) false
    else tailrecPrime(Math.abs(n / 2))
  }

  def testNumber(n: Int): Either[List[String], Int] = {
    if (n <= 100 && n % 2 == 0 && n >= 0 && testPrime(n)) Right(n)
    else {
      val x = if (n > 100) List("n > 100") else Nil
      val y = if (n % 2 != 0) List("n is not even") else Nil
      val z = if (n < 0) List("n is negative") else Nil
      val c = if (!testPrime(n)) List("n is not prime") else Nil

      Left(x ++ y ++ z ++ c)
    }
  }

  implicit val combineIntMax: Semigroup[Int] = Semigroup.instance[Int](Math.max)

  def validateNumber(n: Int): Validated[List[String], Int] =
    Validated.cond(n % 2 == 0, n, List("n is not even"))
      .combine(Validated.cond(n <= 100, n, List("n > 100")))
      .combine(Validated.cond(n >= 0, n, List("n is negative")))
      .combine(Validated.cond(testPrime(n), n, List("n is not prime")))

  // chain
  validValue.andThen(_ => invalidValue)
  // test a valid value
  validValue.ensure(List("Something went wrong!"))(_ % 2 == 0)
  // transform
  validValue.map(_ + 1)
  validValue.leftMap(_.length)
  validValue.bimap(_.length, _ + 1)
  // Either, Optiion, Try -- to --> Validated
  val eitherToValidated: Validated[List[String], Int] = Validated.fromEither(Right(24))
  val optionToValidated: Validated[List[String], Int] = Validated.fromOption(None, List("Nothing present here"))
  val tryToValidated: Validated[Throwable, Int] = Validated.fromTry(Try("something".toInt))
  // backwards
  validValue.toOption
  validValue.toEither

  // Exercise
  object FormValidator {
    type FormValidator[T] = Validated[List[String], T]

    /**
     * fields are:
     * - name
     * - email
     * - password
     *
     * rule are:
     * - name, email, password MUST be specified
     * - name MUST NOT BE blank
     * - email must have @
     * - pass must have at least 10 characters
     */
    // my solution
    def validateForm(form: Map[String, String]): FormValidator[String] = {
      val fieldAreSpecifiedCond = form.keys.toList.forall(List("name", "email", "pass").contains)
      val nameIsNotBlankCond = form.get("name").exists(_.nonEmpty)
      val emailHasSignCond = form.get("email").exists(_.contains("@"))
      val passHasAtLeat10CharactersCond = form.get("pass").exists(_.length >= 10)

      val valid = "Success"
      implicit val combineString: Semigroup[String] = Semigroup.instance((x, _) => x)
      Validated.cond(fieldAreSpecifiedCond, valid, List("All fields have to be specified"))
        .combine(Validated.cond(nameIsNotBlankCond, valid, List("Name must not be blank")))
        .combine(Validated.cond(emailHasSignCond, valid, List("Email must contain @")))
        .combine(Validated.cond(passHasAtLeat10CharactersCond, valid, List("Pass must have at least 10 character")))
    }

    // better solution - more reusable parts
    def getValue(form: Map[String, String], fieldName: String): FormValidator[String] =
      Validated.fromOption(form.get(fieldName), List(s"The field $fieldName must be specified."))

    def nonBlank(value: String, fieldName: String): FormValidator[String] =
      Validated.cond(value.nonEmpty, value, List(s"The field $fieldName must not be blank."))

    def emailProperForm(email: String): FormValidator[String] =
      Validated.cond(email.contains("@"), email, List("Email is invalid."))

    def passwordCheck(pass: String): FormValidator[String] =
      Validated.cond(pass.length >= 10, pass, List("Password must contain at least 10 characters."))


    def validateFormBetter(form: Map[String, String]): FormValidator[String] = {
      getValue(form, "name").andThen(name => nonBlank(name, "name"))
        .combine(getValue(form, "email").andThen(emailProperForm))
        .combine(getValue(form, "pass").andThen(passwordCheck))
        .map(_ => "Success")
    }
  }

  // Extension methods
  val ValidNumber: Validated[List[String], Int] = 42.valid[List[String]]
  val invalid: Validated[String, Int] = "Something went wrong".invalid[Int]

  def main(args: Array[String]): Unit = {

    val form: Map[String, String] = Map(
      "name" -> "",
      "email" -> "asas.pl",
      "pass" -> "12dfgdfgdfgdfg"
    )
    println(FormValidator.validateForm(form))
    println(FormValidator.validateFormBetter(form))

  }
}
