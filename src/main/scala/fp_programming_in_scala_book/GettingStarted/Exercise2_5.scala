package fp_programming_in_scala_book.GettingStarted

/**
 * Implement the higher-order function that composes two functions
 */

object Exercise2_5 extends App{
  def compose[A, B, C](f: B => C, g: A => B): A => C =
    a => f(g(a))
}
