package cats

import cats.implicits.catsSyntaxSemigroup

object Semigroups {

  // Semigroups COMBINE elements of the same type
  val naturalIntSemigroup = Semigroup[Int]
  val intCombination = naturalIntSemigroup.combine(2, 46) // addition

  val naturalStringSemigroup = Semigroup[String]
  val stringCombination = naturalStringSemigroup.combine("AAA", "BBB")

  // specific API - SG not needed, we can use list.reduce(_ + _)
  def reduceInt(list: List[Int]): Int = list.reduce(naturalIntSemigroup.combine)

  // general API
  def reduceThings[T](list: List[T])(implicit semigroup: Semigroup[T]): T = list.reduce(semigroup.combine)

  // semigroup for new type
  case class Expense(id: Long, amount: Double)

  implicit val expenseSemigroup: Semigroup[Expense] = Semigroup.instance[Expense] { (ex1, ex2) =>
    Expense(Math.max(ex1.id, ex2.id), ex1.amount + ex2.amount)
  }

  // extension methods for Semigroup
  val intSum = 2 |+| 3 // requires an implicit SG of Int
  val expenseSum = Expense(1, 122) |+| Expense(2, 234)

  // [T: Semigroup] - implicit parameter of type Semigroup[T]
  def reduceThings2[T: Semigroup](list: List[T]) = list.reduce(_ |+| _)

  def main(args: Array[String]): Unit = {
    println(intCombination)
    println(stringCombination)

    val numberOptions: List[Option[Int]] = (1 to 10).toList.map(Option(_))
    println(reduceThings(numberOptions)) // compiler will produce an implicit Semigroup[Option[Int[]
  }
}