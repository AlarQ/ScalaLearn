package rock_the_JVM.scala_interview

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NumberProblemsSpec extends AnyFlatSpec with Matchers {

  "isPrime method" should "return true if number is prime" in {
    NumberProblems.isPrime(31) shouldBe true
  }

  "isPrime method" should "return false if number is not prime" in {
    NumberProblems.isPrime(32) shouldBe false
  }

  "decompose method" should "return constituent prime divisors" in {
    NumberProblems.decompose(45) shouldBe List(5, 3, 3)
    NumberProblems.decompose(72) shouldBe List(3, 3, 2, 2, 2)
  }

  "approximatePi method" should "approximate PI number" in {
    println(NumberProblems.approximatePi(100000))
  }
}