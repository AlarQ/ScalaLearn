package designpatterns

/**
 * The decorator pattern is used to extend functionality of some object,
 * without affecting other instances of the same class.
 * Decorators provide a flexible alternative to subclassing.
 */
object DecoratorPattern {
  trait OutputStream {
    def write(b: Byte)
    def write(b: Array[Byte])
  }

  class FileOutputStream(path: String) extends OutputStream { /* ... */

    override def write(b: Byte): Unit = ???

    override def write(b: Array[Byte]): Unit = ???
  }

  trait Buffering extends OutputStream {
    abstract override def write(b: Byte) {
      // ...
      super.write(b)
    }
  }

  new FileOutputStream("foo.txt") with Buffering // with Filtering, ...
}
