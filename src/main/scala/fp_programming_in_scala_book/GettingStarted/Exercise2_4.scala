package fp_programming_in_scala_book.GettingStarted

object Exercise2_4 {
  def uncurry[A,B,C](f: A => B => C): (A, B) => C = {
    (a,b) => f(a)(b)
  }
}
