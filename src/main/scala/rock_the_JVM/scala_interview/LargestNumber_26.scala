package rock_the_JVM.scala_interview

import scala.annotation.tailrec

object LargestNumber_26 {

  // ADVICE - very often when you have sorting challenge just implement ordering!
  // ordering rules
  // - reflexive: a <= a
  // - antisymmetric if a <= n && b <= a => a == b - NOT ACHIEVED BELOW
  // - transitive if a <= b && b <=c => a <= c
  def largestNumber(numbers: List[Int]): String = {

    implicit val ordering: Ordering[Int] = Ordering.fromLessThan { (a, b) =>
      val aString = a.toString
      val bString = b.toString
      (aString + bString).compareTo(bString + aString) >= 0
    }

    val largest = numbers.sorted.mkString
    if (largest.charAt(0) == '0') "0"
    else largest
  }
}
