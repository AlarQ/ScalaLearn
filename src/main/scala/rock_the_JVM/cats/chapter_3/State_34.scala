package rock_the_JVM.cats.chapter_3

import cats.data.State

object State_34 {

  // S - type of state
  // A - answer, desirable value, which we obtain after a single computation
  type MyState[S, A] = S => (S, A)

  // State is a wrapper over a single function
  val countAndSay: State[Int, String] = State(currentCount => (currentCount + 1, s"Counted $currentCount"))
  val (eleven, counted10) = countAndSay.run(10).value

  // state is an abstraction for iterative computations

  var a = 10
  a += 1
  val firstComputation = s"Added 1 to 10 obtained ${a}"
    a *= 5
  val secondComputation = s"Multiplied with 5, obtained ${a}"

  // we can rewrite this in FP with states
  val firstComputationPure: State[Int, String] = State((s: Int) => (s + 1, s"Added 1 to 10 obtained ${s + 1}"))
  val secondComputationPure: State[Int, String] = State((s: Int) => (s * 5, s"Multiplied with 5, obtained ${s * 5}"))
  val compositeComputation: State[Int, (String, String)] = firstComputationPure.flatMap(firstResult =>
    secondComputationPure.map(secondResult => (firstResult, secondResult))
  )

  // identical
  val compositeTransformation2 = for {
    firstResult <- firstComputationPure
    secondResult <- secondComputationPure
  } yield (firstResult, secondResult)

  // TODO: an online store
  case class ShoppingCart(items: List[String], total: Double)

  def addToCart(item: String, price: Double): State[ShoppingCart, Double] = State(cart =>
    (cart.copy(items = cart.items :+ item), cart.total + price)
  )

  val myCart: State[ShoppingCart, Double] = for {
    _ <- addToCart("Bread", 20)
    _ <- addToCart("Milk", 10)
    total <- addToCart("Elem", 12)
  } yield total

  // TODO: pure mental gymnastics


  // returns a State data structure, that, when run, will not change the state
  // but will issue the value f(a)
  def inspect[A, B](f: A => B): State[A, B] = State(state => (state, f(state)))

  // returns a State data structure, that, when run, returns the value of that state
  // and make no changes
  def get[A]: State[A, A] = State(state => (state, state))

  // returns a State data structure, that, when run, returns Unit
  // and sets the state to that value
  def set[A](value: A): State[A, Unit] = State(_ => (value, ()))

  // returns a State data structure, that, when run, will return Unit
  // and sets the state to f(state)
  def modify[A](f: A => A): State[A, Unit] = State(state => (f(state),()))

  // methods above are already implemented in companion object of State

  val program: State[Int,(Int,Int,Int)]= for{
    a <- get[Int]
    _ <- set[Int](a + 10)
    b <- get[Int]
    _ <- modify[Int](_ + 4)
    c <- inspect[Int,Int](_ * 2)
  } yield (a,b,c)

  def main(args: Array[String]): Unit = {

  }
}
