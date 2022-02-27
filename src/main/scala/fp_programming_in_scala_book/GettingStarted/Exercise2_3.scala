package fp_programming_in_scala_book.GettingStarted

/**
 * Write this implementation.
 * def curry[A,B,C](f: (A, B) => C): A => (B => C)
 */
object Exercise2_3 {
  def curry[A, B, C](f: (A, B) => C): A => (B => C) = {
    a => b => f(a, b)
  }
}
