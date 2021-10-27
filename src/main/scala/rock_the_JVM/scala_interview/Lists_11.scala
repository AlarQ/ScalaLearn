package rock_the_JVM.scala_interview

import scala.annotation.tailrec

/**
 * in file lessons: 1.1, 1.2
 *
 */

sealed abstract class RList[+T] // our list
{
  def head: T

  def tail: RList[T]

  def isEmpty: Boolean

  def headOption: Option[T]

  def ::[S >: T](elem: S): RList[S] = new ::(elem, this)

  def apply(index: Int): T

  def length: Int

  def reverse: RList[T]

  def ++[S >: T](anotherList: RList[S]): RList[S]

  def remove(index: Int):RList[T]

  def map[S](f:T=>S): RList[S]
  def flatMap[S](f:T=>RList[S]): RList[S]
  def filter(f: T=>Boolean) : RList[T]

  /**
   * Medium difficulty problems
   */

  // run-length encoding
  def rle: RList[(T,Int)]

  def duplicateEach(times:Int):RList[T]

}

case object RNil extends RList[Nothing] {
  override def head: Nothing = throw new NoSuchElementException // throw is side effect

  override def tail: RList[Nothing] = throw new NoSuchElementException

  override def isEmpty: Boolean = true

  override def headOption: Option[Nothing] = None

  override def apply(index: Int): Nothing = throw new NoSuchElementException

  override def length: Int = 0

  override def reverse: RList[Nothing] = this

  override def toString: String = "[]"

  override def ++[S >: Nothing](anotherList: RList[S]): RList[S] = anotherList

  override def remove(index: Int): RList[Nothing] = this

  override def map[S](f: Nothing => S): RList[S] = this

  override def flatMap[S](f: Nothing => RList[S]): RList[S] = this

  override def filter(f: Nothing => Boolean): RList[Nothing] = this

  /**
   * Medium difficulty problems
   */
  override def rle: RList[(Nothing, Int)] = RNil

  override def duplicateEach(times: Int): RList[Nothing] = this
}

case class ::[+T](override val head: T, override val tail: RList[T]) extends RList[T] {
  override def isEmpty: Boolean = false

  override def headOption: Option[T] = Some(head)

  override def apply(index: Int): T = {
    @tailrec
    def loop(list: RList[T], index: Int): T = {
      if (index < 0) throw new NoSuchElementException
      else if (index == 0) list.head
      else loop(list.tail, index - 1)
    }

    loop(this, index)
  }

  def applyV2(index: Int) = {
    @tailrec
    def applyTailRec(remaining: RList[T], currentIndex: Int): T = {
      if (currentIndex == index) remaining.head
      else applyTailRec(remaining.tail, currentIndex + 1)
    }

    if (index < 0) throw new NoSuchElementException
    else applyTailRec(this, 0)
  }

  override def length: Int = {
    @tailrec
    def lengthTailRec(remaining: RList[T], acc: Int): Int = {
      if (remaining.isEmpty) acc
      else lengthTailRec(remaining.tail, acc + 1)
    }

    lengthTailRec(this, 0)
  }

  override def reverse: RList[T] = {
    @tailrec
    def reverseTailRec(remaining: RList[T], acc: RList[T]): RList[T] = {
      if (remaining.isEmpty) acc
      else reverseTailRec(remaining.tail, remaining.head :: acc)
    }

    reverseTailRec(this, RNil)
  }

  override def toString: String = {
    @tailrec
    def toStringTailRec(remaining: RList[T], result: String): String = {
      if (remaining.isEmpty) result
      else if (remaining.tail.isEmpty) s"$result${remaining.head}"
      else toStringTailRec(remaining.tail, s"$result${remaining.head}, ")
    }

    "[" + toStringTailRec(this, "") + "]"
  }

  override def ++[S >: T](anotherList: RList[S]): RList[S] = {
    @tailrec
    def concatTailRec(anotherList: RList[S], acc: RList[S]): RList[S] = {
      if (anotherList.isEmpty) acc
      else concatTailRec(anotherList.tail, anotherList.head :: acc)
    }
    concatTailRec(anotherList, this.reverse).reverse
  }

  override def remove(index: Int): RList[T] = {

    @tailrec
    def removeTailRec(predecessors: RList[T], remaining:RList[T], index:Int) : RList[T] = {
      if(remaining.isEmpty) predecessors.reverse
      else if(index == 0) predecessors.reverse ++ remaining.tail
      else removeTailRec(remaining.head :: predecessors, remaining.tail, index - 1)
    }

    removeTailRec(RNil,this,index)
  }

  override def map[S](f: T => S): RList[S] = {
    @tailrec
    def mapTailRec(remaining:RList[T], acc:RList[S]):RList[S] = {
      if(remaining.isEmpty) acc.reverse
      else mapTailRec(remaining.tail, f(remaining.head) :: acc)
    }

    mapTailRec(this,RNil)
  }

  override def flatMap[S](f: T => RList[S]): RList[S] = {
    @tailrec
    def flatMapTailrec(remaining:RList[T], acc:RList[S]):RList[S] = {
      if(remaining.isEmpty) acc.reverse
      else flatMapTailrec(remaining.tail, f(remaining.head).reverse ++ acc)
    }

    flatMapTailrec(this, RNil)
  }

  override def filter(f: T => Boolean): RList[T] = {
    @tailrec
    def filterTailrec(remaining:RList[T],acc:RList[T]):RList[T] = {
      if(remaining.isEmpty) acc.reverse
      else if(f(remaining.head))
        filterTailrec(remaining.tail, remaining.head :: acc)
      else
        filterTailrec(remaining.tail,acc)
    }

    filterTailrec(this, RNil)
  }

  /**
   * Medium difficulty problems
   */
  override def rle: RList[(T, Int)] = {
    @tailrec
    def rleTailRec(remaining:RList[T], currentTuple: (T,Int), duplicateCount:RList[(T,Int)]):RList[(T,Int)] = {
      if(remaining.isEmpty) duplicateCount.reverse
      else if(remaining.head == currentTuple._1) {
        val updateTuple = (remaining.head,currentTuple._2 + 1)
        rleTailRec(remaining.tail,updateTuple, updateTuple :: duplicateCount.remove(0))
      } else {
        val newTuple = (remaining.head,1)
        rleTailRec(remaining.tail,newTuple,newTuple :: duplicateCount)
      }

     }
    rleTailRec(this,(this.head,0),(this.head,0) :: RNil)
  }

  def rle2: RList[(T,Int)] = {
    @tailrec
    def rle2Tailrec(remaining :RList[T], currentTuple: (T,Int), acc: RList[(T,Int)]): RList[(T,Int)] = {
      if(remaining.isEmpty && currentTuple._2 == 0) acc
      else if(remaining.isEmpty) currentTuple :: RNil
      else if(remaining.head == currentTuple._1) rle2Tailrec(remaining.tail,currentTuple.copy(_2 = currentTuple._2+1),acc)
      else rle2Tailrec(remaining.tail,(remaining.head,1),currentTuple::acc)
    }

    rle2Tailrec(this.tail,(this.head,1),RNil).reverse
  }

  override def duplicateEach(times: Int): RList[T] = {
    @tailrec
    def duplicateTailRec(remaining:RList[T],timesLeft:Int,acc:RList[T]):RList[T] = {
      if(remaining.isEmpty) acc
      else if(timesLeft>0) duplicateTailRec(remaining,timesLeft-1,remaining.head :: acc)
      else duplicateTailRec(remaining.tail,times,acc)
    }

    duplicateTailRec(this,times,RNil).reverse
  }
}


object Lists_11 extends App {
  // acceptable
  println(2 :: RNil)
}
