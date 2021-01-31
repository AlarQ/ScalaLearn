object Numbers {
  object Even {
    def unapply(a: Int): Option[Int] =
      if (a % 2 == 0) Option(a)
      else None
  }

  object Odd {
    def unapply(a: Int): Option[Int] =
      if (a % 2 == 0) None
      else Option(a)
  }
}


object DivMod3 {
  def unapply(value: Int): Option[(Int, Int)] = {
    val a = value / 3
    val b = value % 3
    Option((a, b))
  }

  def main(args: Array[String]): Unit = {
    val DivMod3(x, y) = 5
    assert((x,y) == (1, 2))
  }
}
