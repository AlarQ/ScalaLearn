package fp_programming_in_scala_book.GettingStarted

import scala.annotation.tailrec

object Exercise2_1 extends App {

  def fib(n: Int): Int = {
    @tailrec
    def loop(n: Int, prev: Int, curr: Int): Int = {
      if (n <= 1) curr
      else loop(n - 1, prev + curr, curr = prev)
    }

    loop(n, 1, 0)
  }

  val x = List(fib(1), fib(2), fib(3), fib(4), fib(5), fib(6))
  println(x)
}
