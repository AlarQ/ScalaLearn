package rock_the_JVM.advanced_scala

case object MonadsExample extends App {

  class Lazy[+A](value: => A) {
    def use: A = value

    def flatMap[B](f: A => Lazy[B]): Lazy[B] = {
      f(value)
    }
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("WOOOOOOOOOOOW!")
    23
  }

  println(lazyInstance)

  val lazyInstanceMapped = lazyInstance.flatMap(x => Lazy(x + 20))

  println(lazyInstanceMapped.use)
}
