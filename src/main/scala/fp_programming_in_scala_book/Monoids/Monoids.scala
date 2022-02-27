package fp_programming_in_scala_book.Monoids

object Monoids {
  /**
   * Monoid consists of:
   * - Some type A,
   * - associative binary operation
   * for all x:A, y:A, z:A op(op(x,y)z) == op(x,op(y,z)
   * - zero: A, identity, for all x: A op(x, zero) == op(zero, x) == x
   */

  trait Monoid[A] {
    def op(a1: A, a2: A): A // satisfies op(op(x,y), z) == op(x, op(y,z))

    def zero: A // Satisfies op(x, zero) == x and op(zero, x) == x
  }

  // example
  val stringMonoid = new Monoid[String] {
    def op(a1: String, a2: String) = a1 + a2

    val zero = ""
  }

  def listMonoid[A] = new Monoid[List[A]] {
    def op(a1: List[A], a2: List[A]) = a1 ++ a2

    val zero = Nil
  }

  /**
   * Exercise 10.1: Give Monoid instances for integer addition and multiplication as well as the Boolean
   * operators.
   */

  val intAddition: Monoid[Int] = new Monoid[Int] {
    override def op(a1: Int, a2: Int): Int = a1 + a2

    override def zero: Int = 0
  }
  val intMultiplication: Monoid[Int] = new Monoid[Int] {
    override def op(a1: Int, a2: Int): Int = a1 * a2

    override def zero: Int = 1
  }
  val booleanOr: Monoid[Boolean] = new Monoid[Boolean] {
    override def op(a1: Boolean, a2: Boolean): Boolean = a1 || a2

    override def zero: Boolean = false
  }
  val booleanAnd: Monoid[Boolean] = new Monoid[Boolean] {
    override def op(a1: Boolean, a2: Boolean): Boolean = a1 && a2

    override def zero: Boolean = true
  }

  /**
   * Exercise 10.2: Give a Monoid instance for combining Option values.
   */

  def optionMonoid[A]: Monoid[Option[A]] = new Monoid[Option[A]] {
    override def op(a1: Option[A], a2: Option[A]): Option[A] = a1 orElse a2

    override def zero: Option[A] = None
  }

  // We can get the dual of any monoid just by flipping the `op`.
  def dual[A](m: Monoid[A]): Monoid[A] = new Monoid[A] {
    def op(x: A, y: A): A = m.op(y, x)

    val zero = m.zero
  }

  // Now we can have both monoids on hand
  def firstOptionMonoid[A]: Monoid[Option[A]] = optionMonoid[A]

  def lastOptionMonoid[A]: Monoid[Option[A]] = dual(firstOptionMonoid)

  /**
   * Exercise 10.3: A function having the same argument and return type is sometimes called an endofunction.
   * Write a monoid for endofunctions
   */

  def endoMonoid[A]: Monoid[A => A] = new Monoid[A => A] {
    override def op(a1: A => A, a2: A => A): A => A = a1 compose a2

    override def zero: A => A = (x: A) => x
  }

  /**
   * Exercise  10.5: Implement foldMap
   */
  def foldMap[A, B](as: List[A], m: Monoid[B])(f: A => B): B =
    as.map(f).foldLeft(m.zero)(m.op)

  // Notice that this function does not require the use of `map` at all.
  // All we need is `foldLeft`.
  def foldMap1[A, B](as: List[A], m: Monoid[B])(f: A => B): B =
    as.foldLeft(m.zero)((b, a) => m.op(b, f(a)))

  /**
   * Exercise 10.6*:  The foldMap function can be implemented using either foldLeft or foldRight.
   * But you can also write foldLeft and foldRight using foldMap! Try it.
   */
  // The function type `(A, B) => B`, when curried, is `A => (B => B)`.
  // And of course, `B => B` is a monoid for any `B` (via function composition).
  def foldRight[A, B](as: List[A])(z: B)(f: (A, B) => B): B =
    foldMap(as, endoMonoid[B])(f.curried)(z)

  /** Exercise 10.7: Implement a foldMap for IndexedSeq
   * Your implementation should use the strategy
   * of splitting the sequence in two, recursively processing each half, and then adding the
   * answers together with the monoid. */
  def foldMapV[A, B](v: IndexedSeq[A], m: Monoid[B])(f: A => B): B = {
    if (v.isEmpty)
      m.zero
    else if (v.length == 1)
      f(v(0))
      else {
      val length = v.length
      val (v1, v2) = v.splitAt(length / 2)
      m.op(foldMapV(v1,m)(f),foldMapV(v2,m)(f))
    }

  }

  trait Foldable[F[_]] {
    def foldRight[A,B](as: F[A])(z: B)(f: (A,B) => B): B
    def foldLeft[A,B](as: F[A])(z: B)(f: (B,A) => B): B
    def foldMap[A,B](as: F[A])(f: A => B)(mb: Monoid[B]): B
    def concatenate[A](as: F[A])(m: Monoid[A]): A =
      foldLeft(as)(m.zero)(m.op)
  }
}
