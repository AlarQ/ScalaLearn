package randomNotes

import scala.collection.mutable.ListBuffer


trait Stack[T] {

  def push(t: T): Unit

  def pop(): Option[T]

  def top: Option[T]
}


trait IndexedStack[T] extends Stack[T] {
  def apply(idx: Int): Option[T]

  def update(idx: Int, value: T): Unit
}


trait StackOps[T] extends Stack[T] {
  def pushAll(l: List[T]): Unit = {
    for (x <- l) {
      push(x)
    }
  }

  def removeAll(): Unit = while (pop().nonEmpty) {}
}

class StandardStack[T] extends Stack[T] with IndexedStack[T] with StackOps[T] {
  private[this] val buffer: ListBuffer[T] = ListBuffer.empty

  override def top: Option[T] = buffer.headOption

  override def push(t: T): Unit = {
    buffer.prepend(t)
  }

  override def pop(): Option[T] = {
    val ret = buffer.headOption
    buffer.dropInPlace(1)
    ret
  }

  override def apply(idx: Int): Option[T] = buffer.lift(idx)

  override def update(idx: Int, value: T): Unit = buffer.update(idx, value)
}


trait DoublingStack[T] extends Stack[T] {
  // super.push will call method,
  // which will be implemented
  // by class implementing this trait
  abstract override def push(t: T): Unit = {
    super.push(t)
    super.push(t)
  }
}




class StringStack extends StandardStack[String] with DoublingStack[String] {

  override def push(t: String): Unit = {
    println(s"Adding element ${t}")
    super.push(t)
  }
}

object Stack {
  def doubling[T]: Stack[T] = new StandardStack[T] with DoublingStack[T]
}


object AllStack {
  def main(args: Array[String]): Unit = {
    val stack = new StringStack
    stack.push("Hello!")
    stack.push("World!")
    stack.push("")

    while (stack.top.nonEmpty)
      println(stack.pop())
  }
}
