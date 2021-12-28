package rock_the_JVM.scala_interview

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ListSpec extends AnyFlatSpec with Matchers {

  val list = 1 :: 2 :: 3 :: 4 :: RNil

  "apply method" should "return value for index in list bounds " in {
    list(3) shouldBe 4
  }

  "apply method" should "throw NoSuchElementException for index out of list bounds " in {
    an[NoSuchElementException] should be thrownBy list(5)
  }

  "length method" should "return length of list" in {
    list.length shouldBe 4
  }

  "reverse method" should "return reversed list" in {
    list.reverse shouldBe 4 :: 3 :: 2 :: 1 :: RNil
  }

  "++ method" should "return concatenated lists" in {
    val anotherList = 4 :: 5 :: 6 :: RNil
    list ++ anotherList shouldBe 1 :: 2 :: 3 :: 4 :: 4 :: 5 :: 6 :: RNil
  }

  "remove method" should "element when index is in list bounds" in {
    list.remove(2) shouldBe 1 :: 2 :: 4 :: RNil
  }

  "remove method" should "return same list, if index is out of list bounds" in {
    list.remove(7) shouldBe list
  }

  "remove method" should "return same list, if index is <  0>" in {
    list.remove(-7) shouldBe list
  }

  "map method" should "return mapped list" in {
    list.map(_ + 1) shouldBe 2 :: 3 :: 4 :: 5 :: RNil
  }

  "flatMap method" should "return mapped list" in {
    list.flatMap(elem => ::(elem, elem * 2 :: RNil)) shouldBe 1 :: 2 :: 2 :: 4 :: 3 :: 6 :: 4 :: 8 :: RNil
  }

  "filter method" should "return filtered list" in {
    list.filter(_ % 2 == 0) shouldBe 2 :: 4 :: RNil
  }

  "rle method" should "return counted duplicates (compressed list)" in {
    (1 :: 1 :: 2 :: 2 :: 2 :: 2 :: 3 :: 3 :: 3 :: 4 :: RNil).rle shouldBe (1, 2) :: (2, 4) :: (3, 3) :: (4, 1) :: RNil
  }

  "duplicateEach method" should "duplicate list elements n times" in {
    list.duplicateEach(3) shouldBe
      1 :: 1 :: 1 :: 2 :: 2 :: 2 :: 3 :: 3 :: 3 :: 4 :: 4 :: 4 :: RNil
  }

  // TODO repeat
  "rotate method" should "should rotate list by n times" in {
    list.rotate(3) shouldBe 4 :: 1 :: 2 :: 3 ::RNil
  }

  "sample method" should "return randomly k elements from list" in {
    val samples = list.sample(2)
    println(samples)
    samples.length shouldBe 2
  }

  "sorted method" should "sort a list with given ordering" in {
    val ordering: Ordering[Int] = Ordering.Int
    val listToSort = 3 :: 1 :: 5 :: 6 :: 2 :: 3 :: RNil
    listToSort.sorted(ordering) shouldBe 1 :: 2 :: 3 :: 3 :: 5 ::6 :: RNil
  }
}
