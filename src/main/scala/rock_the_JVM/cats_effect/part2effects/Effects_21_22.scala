package rock_the_JVM.cats_effect.part2effects

import java.util.Calendar
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn

// TODO: WATCH AGAIN
object Effects_21_22 {

  // referential transparency - can replace an expression with its value
  // as many times as we want without changing behaviour

  // In program with side effects referential transparency is broken
  val printSomething: Unit = println("Boo!")
  val printSomething_v2: Unit = () // not the same

  // side effects are inevitable for useful programs

  // effect - data type, that embodies side effect
  // it must tell us what (effect properties):
  // - type signature describes the kind of calculation that will be performed
  // - what kind of value will be produced by that calculation
  // - when side effects are needed, effect construction is separate from effect execution

  // example: it is a monad
  // properties - OK -> it is Effect data type
  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] =
      MyIO(() => f(unsafeRun()))

    def flatMap[B](f: A => MyIO[B]): MyIO[B] =
      MyIO(() => f(unsafeRun()).unsafeRun())
  }

  val myIO: MyIO[Int] = MyIO(() => {
    println("Something...")
    43
  })

  /**
   * Exercises
   * 1. An IO which return the current time system
   * 2. An IO which measures the duration of a computation
   * 3. An IO which prints something to the console
   * 4. Am IO which reads a line from the std.input
   */

  // 1
  val clock: MyIO[Long] = MyIO[Long](() => System.currentTimeMillis())

  // 2
  def measure[A](computation: MyIO[A]): MyIO[Long] = for {
    start <- clock
    _ <- computation
    end <- clock
  } yield end - start

  /*
    // the same - *
    clock.flatMap(start => computation.flatMap(_ => clock.map(end => end - start)

    // Breakdown of operations
    > clock.map(end => end - start) = MyIO(() => clock.unsafeRun() - start)
                              ||
    > clock.map(end => end - start) = MyIO(() => System.currentTimeMillis() - start)

   // use above breakdown in *
    clock.flatMap(start => computation.flatMap(_ => MyIO(() => System.currentTimeMillis() - start)))

    // breakdown continue
    > computation.flatMap(lambda) = MyIO(() => lambda(__COMP__).unsafeRun())
                                ||
    > computation.flatMap(lambda) = MyIO(() => MyIO(() => System.currentTimeMillis() - start).unsafeRun())
                                ||
    > computation.flatMap(lambda) = MyIO(() => MyIO(() => System.currentTimeMillis()_after__COMP__() - start)

    // breakdown continue
    clock.flatMap(start => MyIO(() => MyIO(() => System.currentTimeMillis()_after__COMP__() - start))
    = MyIO(() => lambda(clock.unsafeRun()).unsafeRun())
    = MyIO(() => MyIO(() => System.currentTimeMillis()_after__COMP__() - start).unsafeRun())
    = MyIO(() => MyIO(() => System.currentTimeMillis()_after__COMP__() - System.currentTimeMillis()).unsafeRun())
    = MyIO(() => MyIO(() => System.currentTimeMillis()_after__COMP__() - System.currentTimeMillis()))
   */

  def testTimeIO(): Unit = {
    val test = measure(MyIO(() => Thread.sleep(1000)))
    println(test.unsafeRun())
  }

  // 3
  def putStrLn(line: String): MyIO[Unit] = MyIO(() => println(line))

  // 4
  val read: MyIO[String] = MyIO(() => StdIn.readLine())

  def testConsole(): Unit = {
    val program: MyIO[Unit] = for {
      line1 <- read
      line2 <- read
      _ <- putStrLn(line1 + line2)
    } yield ()

    program.unsafeRun()
  }

  def main(args: Array[String]): Unit = {
    testConsole()
  }

}


