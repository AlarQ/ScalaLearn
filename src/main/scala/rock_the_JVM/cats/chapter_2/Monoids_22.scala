package rock_the_JVM.cats.chapter_2

import cats.implicits.catsSyntaxSemigroup
import cats.kernel.Monoid

object Monoids_22 {
  val numbers = (1 to 1000).toList
  // |+| is always associative
  val someLeft = numbers.foldLeft(0)(_ |+| _)
  val someRight = numbers.foldRight(0)(_ |+| _)

  // define general API
  // Semigroup not enough
  //  def combineFold[T:Semigroup](list: List[T]) : T = list.foldLeft( /* What */ )(_ |+| _)

  // MONOIDS
  val intMonoid = Monoid[Int]
  val combineInt = intMonoid.combine(12, 122)
  val zero = intMonoid.empty // neutral element

  def combineFold[T](list: List[T])(implicit monoid: Monoid[T]): T = list.foldLeft(monoid.empty)(_ |+| _)

  // another example
  val phoneBooks = List(
    Map(
      "Alice" -> 123,
      "Bob" -> 345
    ),
    Map(
      "Charlie" -> 345,
      "Tom" -> 122
    )
  )

  // another example
  case class ShoppingCart(items: List[String], total: Double)

  implicit val shoppingMonoid = Monoid.instance[ShoppingCart](ShoppingCart(Nil, 0), { (sc1, sc2) =>
    ShoppingCart(sc1.items ++ sc2.items, sc1.total + sc2.total)
  })

  val shopCarts = List(
    ShoppingCart(List("book", "house"), 12),
    ShoppingCart(List("mouse", "chicken"), 34)
  )

  def checkout(shoppingCarts: List[ShoppingCart]) = combineFold(shoppingCarts)

  def main(args: Array[String]): Unit = {
    println(someLeft)
    println(someRight)
    println(combineFold(phoneBooks))
    println(checkout(shopCarts))
  }
}
