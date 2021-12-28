package rock_the_JVM.scala_interview

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rock_the_JVM.scala_interview.LargestNumber_26.largestNumber

class LargestNumberSpec extends AnyFlatSpec with Matchers {



  "largestNumber method" should "return largest number concatenated from list" in {
     largestNumber(List(9, 5, 3, 34, 30)) shouldBe "9534330"
  }
}