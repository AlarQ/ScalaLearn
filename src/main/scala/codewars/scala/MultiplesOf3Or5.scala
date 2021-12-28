package codewars.scala

import scala.annotation.tailrec

/**
 * If we list all the natural numbers below 10 that are multiples of 3 or 5,
 * we get 3, 5, 6 and 9. The sum of these multiples is 23.
 *
 * Finish the solution so that it returns the sum of all the multiples of 3 or 5 below the number passed in.
 * Additionally, if the number is negative, return 0 (for languages that do have them).
 *
 * Note: If the number is a multiple of both 3 and 5, only count it once.
 */
object MultiplesOf3Or5 {
  def solution(number: Int): Long = {

    @tailrec
    def loop(current: Int, acc: Long): Long = {
      if (current >= number) acc
      else if (current % 3 == 0 && current % 5 == 0) loop(current + 1, acc + current)
      else if (current % 3 == 0) loop(current + 1, acc + current)
      else if (current % 5 == 0) loop(current + 1, acc + current)
      else loop(current + 1, acc)
    }

    if (number < 3) 0
    else loop(3, 0)
  }

  def main(args: Array[String]): Unit = {
    //    println("input: 2, output: " + solution(2))
    println("input: 10, output: " + solution(10))
    //    println("input: 15, output: " + solution(15))
    //    println("input: 23, output: " + solution(23))
    //    println("input: 30, output: " + solution(30))
  }

}
