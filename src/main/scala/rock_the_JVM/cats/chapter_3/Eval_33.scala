package rock_the_JVM.cats.chapter_3

import cats.Eval

object Eval_33 {
  /**
   * Cats makes the distinction between
   * - 1 - evaluating an expression eagerly - default in Scala (val x = 2 + 3 // 2+3 is evaluated eagerly)
   * - 2 - evaluating lazily and every time you request it // recomputed every time you need it
   * - 3 - evaluating lazily and keeping the value (memorizing)
   */

  //   - 1 - Evaluating expression eagerly before we may want to use it
  val instantEval: Eval[Int] = Eval.now {
    println("Computing now: ")
    42
  }

  // - 2 - recomputed every time, lazily
  val reDoEval: Eval[Int] = Eval.always {
    println("computing again")
    23
  }

  // - 3 -  evaluating lazily and keeping the value (memorizing)
  val delayEval: Eval[Int] = Eval.later {
    println("Computing later")
    4221
  }

  // we can compose these evaluations
  val composedEval: Eval[Int] = instantEval.flatMap(value => delayEval.map(_ + value))

  // identical
  val anotherComposedEval: Eval[Int] = for {
    value1 <- instantEval
    value2 <- delayEval
  } yield value1 + value2

  // We can memoize a value, without of need of recompute all the time
  val dontRecompute: Eval[Int] = reDoEval.memoize

  val tutorial: Eval[String] = Eval
    .always {
      println("Step 1...");
      "put the guitar on your lap"
    }
    .map { step1 => println("Step 2..."); s"$step1, then put your left hand on the neck" }
    .memoize // remember the value up to this point
    .map { step12 => println("Step 3..."); s"$step12, then with the right hand strike the strings" }

  /**
   * Step 1...
   * Step 2...
   * Step 3...
   * put the guitar on your lap, then put your left hand on the neck, then with the right hand strike the strings
   * Step 3...
   * put the guitar on your lap, then put your left hand on the neck, then with the right hand strike the strings
   */

  // Exercise
  // - defer(Eval.now) does NOT run the side effects
  def defer[T](eval: => Eval[T]): Eval[T] = Eval.later(()).flatMap(_ => eval)

  // Exercise
  // rewrite the method with Evals
  def reverseList[T](list: List[T]): List[T] =
    if (list.isEmpty) list
    else reverseList(list.tail) :+ list.head

  def reverseListEval[T](list: List[T]): Eval[List[T]] = {
    if (list.isEmpty) Eval.now(list)
    else reverseListEval(list.tail).map(_ :+ list.head)
  }

  // we can make this stack-safe
  // because Eval.later is evaluated in tail-recursive way -> in Eval class we have @tailrec methods
  def reverseListEvalStackSafe[T](list: List[T]): Eval[List[T]] = {
    if (list.isEmpty) Eval.now(list)
    else defer(reverseListEval(list.tail).map(_ :+ list.head))
  }

  def main(args: Array[String]): Unit = {
    defer(Eval.now {
      println("don't print that!")
      43
    })
  }
}
