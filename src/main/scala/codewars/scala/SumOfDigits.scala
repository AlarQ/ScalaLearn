package codewars.scala

import scala.annotation.tailrec

object SumOfDigits {

  def digitalRoot(n: Int): Int = {
    @tailrec
    def loop(n:Int): Int = {
      if(noDigits(n) == 1) n
      else {
        val digits = n.toString.split("").map(Integer.parseInt)
        val sum = digits.sum
        loop(sum)
      }
    }

    def noDigits(n: Int): Int = n.toString.length

    loop(n)
  }
}
