package odersky_course

import scala.annotation.tailrec

object Week_1_Functions_Evaluations extends App {

  println("Pascal's Triangle")
  for (row <- 0 to 10) {
    for (col <- 0 to row)
      print(pascal(col, row) + " ")
    println()
  }

  /**
   * Exercise 1
   */

  def myPascal(c: Int, r: Int): Int = {
    if (c == 0 || r == 0 || c == r) 1
    else if (c > r) 0
    else myPascal(c - 1, r - 1) + myPascal(c, r - 1)

  }

  /**
   * Exercise 2
   */
  def myBalance(chars: List[Char]): Boolean = {

    @tailrec
    def loop(chars: List[Char], count: Int): Boolean = {
      if (chars.isEmpty) count == 0
      else if (count < 0) false
      else {
        if (chars.head == '(') loop(chars.tail, count + 1)
        else if (chars.head == ')') loop(chars.tail, count - 1)
        else loop(chars.tail, count)
      }
    }

    loop(chars, 0)
  }

  val char1 = "(if (zero? x) max (/ 1 x))"
  val char2 = "())("
  println(s"myBalance($char1) " + myBalance(char1.toList))
  println(s"myBalance($char2) " + myBalance(char2.toList))


  def pascal(c: Int, r: Int): Int = {
    if (r == 0 || c == 0 || c == r) 1
    else if (c > r) 0
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }


  def balance(chars: List[Char]): Boolean = {
    balance(chars, 0)
  }

  private def balance(chars: List[Char], unbalancedCount: Int): Boolean = {
    if (chars.isEmpty) unbalancedCount == 0
    else if (unbalancedCount < 0) false
    else {
      val count =
        if (chars.head == '(') unbalancedCount + 1
        else if (chars.head == ')') unbalancedCount - 1
        else unbalancedCount
      balance(chars.tail, count)
    }
  }

  /**
   * Exercise 3
   */
  def myCountChange(money: Int, coins: List[Int]): Int = {
    val sorted = coins.sorted.reverse
    if (money <= 0) 0
    else {
      //TODO
      // spróbować zrobić jak zapomnę
      0
    }
  }

  println(countChange(13,List(1,2,5)))

  def countChange(money: Int, coins: List[Int]): Int = {
    if (money <= 0) 0
    else countChangeAux(money, coins.sorted.reverse)
  }

  private def countChangeAux(money: Int, coins: List[Int]): Int = {
    if (coins.isEmpty || money < 0) 0
    else if (money == 0) 1
    else countChangeAux(money - coins.head, coins) + countChangeAux(money, coins.tail)
  }
}

