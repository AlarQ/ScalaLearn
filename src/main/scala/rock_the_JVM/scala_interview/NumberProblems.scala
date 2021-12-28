package rock_the_JVM.scala_interview

import scala.annotation.tailrec
import scala.util.Random

case object NumberProblems extends App {

  def isPrime(number: Int): Boolean = {
    val sqrt = Math.sqrt(Math.abs(number))

    @tailrec
    def isPrimeTailRec(curr: Int): Boolean = {
      if (curr > sqrt) true
      else if (number % curr == 0) false
      else isPrimeTailRec(curr + 1)
    }

    //ciekawe
    @tailrec
    def tailrec2(curr: Int): Boolean = {
      if (curr > sqrt) true
      else number % curr != 0 && tailrec2(curr + 1)
    }

    isPrimeTailRec(2)
  }

  def decompose(number: Int): List[Int] = {

    @tailrec
    def decomposeTailRec(currentNumber: Int, currentDivisor: Int, acc: List[Int]): List[Int] = {
      if (currentDivisor > Math.sqrt(currentNumber)) currentNumber :: acc
      else if (currentNumber % currentDivisor == 0) decomposeTailRec(currentNumber / currentDivisor, 2, currentDivisor :: acc)
      else decomposeTailRec(currentNumber, currentDivisor + 1, acc)
    }

    decomposeTailRec(number, 2, Nil)
  }

  // ugly, remember to pass seed to random
  def approximatePi(nPoints: Int): Double = {
    val random = new Random(System.currentTimeMillis())

    val randomPoints = Seq.fill(nPoints)((random.nextDouble(),random.nextDouble()))

    @tailrec
    def pointsInACircle(pointsLeft: Seq[(Double, Double)], sum: Int): Int = {
      if (pointsLeft.isEmpty) sum
      else if (pointsLeft.head._1 * pointsLeft.head._1 + pointsLeft.head._2 * pointsLeft.head._2 < 1)
        pointsInACircle(pointsLeft.tail, sum + 1)
      else pointsInACircle(pointsLeft.tail, sum)
    }

    pointsInACircle(randomPoints, 0) * 4.0 / nPoints
  }

  def approximatePiBETTER(nPoints: Int): Double = {
    val random = new Random(System.currentTimeMillis())

    val nPointsInsideCircle =  (1 to nPoints).map { _ =>
      val x = random.nextDouble()
      val y = random.nextDouble()

      x * x + y * y
    }.count(distance => distance < 1)

    nPointsInsideCircle * 4.0 / nPoints
  }
}
