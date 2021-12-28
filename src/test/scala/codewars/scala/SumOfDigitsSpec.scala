package codewars.scala

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import codewars.scala.SumOfDigits._

class SumOfDigitsSpec extends AnyFunSpec with Matchers{

  describe("no of digits"){
    it("should return no digits of number n"){
      digitalRoot(16) shouldBe 7
    }
  }
}
