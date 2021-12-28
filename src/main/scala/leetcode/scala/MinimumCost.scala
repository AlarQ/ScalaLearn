package leetcode.scala

import scala.annotation.tailrec

/**
 * 1217. Minimum Cost to Move Chips to The Same Position
 */

// Znależć pozycję dla której najwięcej odleglosci % 2
// a potem najmniej mod 1
object MinimumCost {
  def minCostToMoveChips(position: Array[Int]): Int = {

    def cost(pos: Int, positionsLeft: Array[Int]): Int = {

      @tailrec
      def loop(positionLeft: Array[Int], acc: Int): Int = {
        if(positionLeft.isEmpty) acc
        else loop(positionLeft.tail,acc + Math.abs(pos - positionLeft.head) % 2)
      }

      loop(positionsLeft,0)
    }

      val costsForPos = for {
        pos <- position
        positionsToProcess = position.filter(_ != pos)

        costForPostion = cost(pos, positionsToProcess)
      } yield (pos,costForPostion)


    costsForPos.map(_._2).min
  }

  def main(args: Array[String]): Unit = {
    val arr1 = Array(1,2,3)
    val arr2 = Array(2,2,2,3,3)
    val arr3 = Array(3,3,1,2,2)
    val arr4 = Array(6,4,7,8,2,10,2,7,9,7)
    println(s"Cost for ${arr1.mkString("Array(", ", ", ")")}: ${minCostToMoveChips(arr1)}")
    println(s"Cost for ${arr2.mkString("Array(", ", ", ")")}: ${minCostToMoveChips(arr2)}")
    println(s"Cost for ${arr3.mkString("Array(", ", ", ")")}: ${minCostToMoveChips(arr3)}")
    println(s"Cost for ${arr4.mkString("Array(", ", ", ")")}: ${minCostToMoveChips(arr4)}")
  }
}


