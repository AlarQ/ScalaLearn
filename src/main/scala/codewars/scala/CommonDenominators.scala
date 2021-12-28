package codewars.scala

object CommonDenominators {

    def convertFrac(lst: Array[(Long, Long)]): String = {
      val simplifiedLst = lst.map(simplifyFrac)
      val commonDenom = simplifiedLst.map(_._2).foldLeft(simplifiedLst.head._2)((x, y) => lcm(x, y))
      val result = for {
        frac <- simplifiedLst
        nom = frac._1
        denom = frac._2
        multiplier = commonDenom / denom
      } yield (nom * multiplier, commonDenom)
      result.foldLeft("")((x, y) => x ++ y.toString())

    }

    def lcm(x: Long, y: Long): Long = {
      x * y / gcd(x, y)
    }

    def gcd(x: Long, y: Long): Long = {
      if (y == 0) x else gcd(y, x % y)
    }

    def simplifyFrac(frac: (Long, Long)): (Long, Long) = {
      val gcdValue = gcd(frac._1, frac._2)
      (frac._1 / gcdValue, frac._2 / gcdValue)
    }

  def main(args: Array[String]): Unit = {
    val arr1: Array[(Long, Long)] = Array((1, 2), (1, 3), (1, 4))
    val arr2: Array[(Long, Long)] = Array((69, 130), (87, 1310), (30, 40))
    val arr3: Array[(Long, Long)] = Array((5, 30), (7, 32))
//    println(convertFrac(arr1))
//    println(convertFrac(arr2))
    println(convertFrac(arr3))
    println(lcm(6,32))
  }
}
