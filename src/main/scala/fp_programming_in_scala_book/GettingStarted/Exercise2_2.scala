package fp_programming_in_scala_book.GettingStarted

/**
 * Implement isSorted, which checks whether an Array[A] is sorted according to a
 * given comparison function:
 */
object Exercise2_2  extends App {
  def isSorted[A](as: Array[A], ordered: (A,A) => Boolean): Boolean={
    def loop(n:Int):Boolean={
        if(n>=as.length-1) true
        else if(ordered(as(n),as(n+1))) loop(n+1)
        else false
    }

    loop(0)
  }

  val p = (x:Int,y:Int) => x<=y
  val as = Array(1,2,3,1)
  println(isSorted(as,p))
}
