package fp_programming_in_scala_book.PurelyFunctionalState

object Exercise6_1 extends App {

  trait RNG {
    def nextInt: (Int, RNG)
  }

  case class SimpleRNG(seed: Long) extends RNG {
    def nextInt: (Int, RNG) = {
      val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
      val nextRNG = SimpleRNG(newSeed)
      val n = (newSeed >>> 16).toInt
      (n, nextRNG)
    }
  }

  /**
   * Ex. 6.1 - Write a function that uses RNG.nextInt to generate a random integer between 0 and
   * Int.maxValue (inclusive)
   */
  def nonNegativeInt(rng: RNG): (Int, RNG) = {
    val nextInt = rng.nextInt
    if (nextInt._1 == Int.MinValue) {
      nonNegativeInt(rng)
    } else {
      nextInt._1.abs -> nextInt._2
    }
  }

  // better
  def nonNegativeInt2(rng: RNG): (Int, RNG) = {
    val (i, r) = rng.nextInt
    (if (i < 0) -(i + 1) else i, r)
  }

  /**
   * Ex. 6.2 - Write a function to generate a Double between 0 and 1, not including 1
   */
  def double(rng: RNG): (Double, RNG) = {
    val (i, r) = nonNegativeInt(rng)
    (i / (Int.MaxValue.toDouble + 1)) -> r
  }

  /**
   * Ex. 6.3 - Write functions to generate an (Int, Double) pair, a (Double, Int) pair, and a
   * (Double, Double, Double) 3-tuple.
   */
  def intDouble(rng: RNG): ((Int, Double), RNG) = {
    val (i, r1) = rng.nextInt
    val (d, r2) = double(r1)
    (i, d) -> r2
  }

  def doubleInt(rng: RNG): ((Double, Int), RNG) = {
    val (i, d) -> r = intDouble(rng)
    (d, i) -> r
  }

  def double3(rng: RNG): ((Double, Double, Double), RNG) = {
    val (d1, r1) = double(rng)
    val (d2, r2) = double(r1)
    val (d3, r3) = double(r2)
    ((d1, d2, d3), r3)
  }

  /**
   * Ex. 6.4 - Write a function to generate a list of random integers.
   */
  def ints(count: Int)(rng: RNG): (List[Int], RNG) = {
    def loop(count: Int, rng: RNG, xs: List[Int]): (Int, (List[Int], RNG)) = {
      if (count == 0) (count, (xs, rng))
      else {
        val (i, r) = rng.nextInt
        loop(count - 1, r, xs :+ i)
      }
    }

    loop(count, rng, Nil)._2
  }

  type Rand[+A] = RNG => (A, RNG)

  val int: Rand[Int] = _.nextInt

  def unit[A](a: A): Rand[A] = rng => (a, rng)

  def map[A, B](s: Rand[A])(f: A => B): Rand[B] =
    rng => {
      val (a, rng2) = s(rng)
      (f(a), rng2)
    }

  def nonNegativeEven: Rand[Int] = map(nonNegativeInt)(i => i - i % 2)

  /**
   * Ex. 6.5 - Use map to reimplement double in a more elegant way.
   */
  def double1: Rand[Double] =
    map(nonNegativeInt)(i => i / (Int.MaxValue.toDouble + 1))

  /**
   * Ex. 6.6 - Write the implementation of map2 based on the following signature. This function
   * takes two actions, ra and rb, and a function f for combining their results, and returns
   * a new action that combines them:
   */
  def map2[A, B, C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = rng => {
    val (n1, rng1) = ra(rng)
    val (n2, rng2) = rb(rng1)
    f(n1, n2) -> rng2
  }

  def both[A, B](ra: Rand[A], rb: Rand[B]): Rand[(A, B)] =
    map2(ra, rb)((_, _))

  val randIntDouble: Rand[(Int, Double)] =
    both(int, double)
  val randDoubleInt: Rand[(Double, Int)] =
    both(double, int)

  /**
   * Ex. 6.7 - Implement sequence for combining a List of transitions into a single
   * transition.
   */
  def sequence[A](fs: List[Rand[A]]): Rand[List[A]] =
    fs.foldRight(unit(List[A]()))((f, acc) => map2(f, acc)(_ :: _))


  def _ints(count: Int): Rand[List[Int]] =
    sequence(List.fill(count)(int))
}