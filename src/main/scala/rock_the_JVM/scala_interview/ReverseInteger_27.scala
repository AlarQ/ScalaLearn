package rock_the_JVM.scala_interview

object ReverseInteger_27  extends  App {

  println(reverseInteger(0))
  println(reverseInteger(9))
  println(reverseInteger(53))
  println(reverseInteger(504))
  println(reverseInteger(540))
  println(reverseInteger(53678534))
  println(reverseInteger(Int.MaxValue))

  println(reverseInteger(-9))
  println(reverseInteger(-53))
  println(reverseInteger(-504))
  println(reverseInteger(-540))
  println(reverseInteger(-53678534))
  println(reverseInteger(Int.MinValue))


  def reverseInteger(int: Int): Int = {

    def reverse(int: Int): Int ={
      val res = int.toString.toCharArray.map(x =>Integer.parseInt(x.toString)).reverse.mkString
      val bigInt = BigInt(res)
      if(bigInt >= Int.MaxValue) 0
      else bigInt.toInt
    }

    if(int >= 0) reverse(int)
    else if (int == Integer.MIN_VALUE) 0
    else -reverse(Math.abs(int))

  }
}
