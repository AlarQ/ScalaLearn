package codewars.scala

import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import codewars.scala.ProductOfConsecutiveFibNnumbers._

class ProdFibSpec extends AnyFunSpec with Matchers with GivenWhenThen {

  describe("prod of fib numbers decomposition") {
    it("decompose prod of fib numbers") {
      productFib(714) shouldBe Array(21, 34, 1)
      productFib(800) shouldBe Array(34, 55, 0)
    }
  }

  describe("n fib number") {
    it("computes nth fib number") {
      fib(0) shouldBe 0
      fib(1) shouldBe 1
      fib(2) shouldBe 1

      fib(5) shouldBe 5
      fib(7) shouldBe 13
      fib(10) shouldBe 55
    }
  }
}
