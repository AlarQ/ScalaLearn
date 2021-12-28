package rock_the_JVM.yt

import rock_the_JVM.yt.ExpressionProblem.{Expr, eval}

/**
 * We have an expression problem:
 * evaluating an expression while maintaining type safety
 * and returning the right value of the right type
 */

object ExpressionProblem {
  trait Expr

  case class B(boolean: Boolean) extends Expr

  case class Or(left: Expr, right: Expr) extends Expr

  case class And(left: Expr, right: Expr) extends Expr

  case class Not(expr: Expr) extends Expr

  val aGiantBoolean: Expr = Or(And(B(true), B(false)), B(false))

  def eval(expr: Expr): Boolean = expr match {
    case B(b) => b
    case Or(a, b) => eval(a) || eval(b)
    case And(a, b) => eval(a) && eval(b)
    case Not(a) => !eval(a)
  }

  // all it is pretty easy UNTIL we add new staff
  case class I(int: Int) extends Expr

  case class Sum(left: Expr, right: Expr) extends Expr
  // now we have to change eval method, but the have different types!
  // so we have to create new method

  def eval_v2(expr: Expr): Any = expr match {
    case B(b) => b
    case Or(a, b) => eval(a) || eval(b)
    case And(a, b) => eval(a) && eval(b)
    case Not(a) => !eval(a)
    // casts needed
  }
}

// #1 - solution one
object Tagging {
  trait Expr {
    val tag: String
  }

  case class B(boolean: Boolean) extends Expr {
    override val tag: String = "bool"
  }

  case class Or(left: Expr, right: Expr) extends Expr{
    override val tag: String = "bool"
  }

  case class And(left: Expr, right: Expr) extends Expr{
    override val tag: String = "bool"
  }

  case class Not(expr: Expr) extends Expr{
    override val tag: String = "bool"
  }

  case class I(int: Int) extends Expr {
    override val tag: String = "int"
  }

  case class Sum(left: Expr, right: Expr) extends Expr {
    override val tag: String = "int"
  }

  def eval(expr: Expr): Any = expr match{
    case B(b) => b
    case Or(left,right) =>
      if(left.tag != "bool" || right.tag != "bool")
        throw new IllegalArgumentException("improper argument type")
      else eval(left).asInstanceOf[Boolean] || eval(right).asInstanceOf[Boolean]
  }

  /**
   * + expressions are validated for correctness at construction
   * - no type safety
   * - errors are shown at runtime
   */
}

// #2 - Solution two
object TaglessInitial{
  trait Expr[A]

  case class B(boolean: Boolean) extends Expr[Boolean]

  case class Or(left: Expr[Boolean], right: Expr[Boolean]) extends Expr[Boolean]

  case class And(left: Expr[Boolean], right: Expr[Boolean]) extends  Expr[Boolean]

  case class Not(expr: Expr[Boolean]) extends Expr[Boolean]

  case class I(int: Int) extends Expr[Int]

  case class Sum(left: Expr[Int], right: Expr[Int]) extends Expr[Int]

  // why dont work?
  def eval[B](expr: Expr[B]): B = expr match {
    case B(b) => b
    case I(i) => i
    case Or(left, right) => eval(left) || eval(right)
    case Sum(left, right) => eval(left) + eval(right)
  }
}

object TaglessFinal{
  trait Expr[A]{
    val value : A //the final value after evaluation
  }
  
  def b(boolean: Boolean): Expr[Boolean] = new Expr[Boolean] {
    override val value: Boolean = boolean
  }

  def i(Int: Int): Expr[Int] = new Expr[Int] {
    override val value: Int = Int
  }

  def or(left: Expr[Boolean], right:Expr[Boolean]): Expr[Boolean] = new Expr[Boolean] {
    override val value: Boolean = left.value || right.value // evaluation happens instantly
  }

  def eval[A](expr: Expr[A]): A = expr.value
}

