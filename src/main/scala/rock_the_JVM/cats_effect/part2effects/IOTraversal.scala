package rock_the_JVM.cats_effect.part2effects

import cats.Traverse
import cats.effect.{IO, IOApp}
import cats.implicits.{catsSyntaxParallelSequence1, catsSyntaxParallelTraverse1}
import rock_the_JVM.cats_effect.utils.DebugWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

object IOTraversal extends IOApp.Simple {

  def heavyComputation(string: String): Future[Int] = Future {
    Thread.sleep(Random.nextInt(1000))
    string.split(" ").length
  }

  val workLoad: List[String] = List("asd asasdasda sadasd", "asd asd adasd asdas", "xcvxcv xcvxc xcv xxcvxcv xcvxcv")
  val listTraverse = Traverse[List]

  def clunkyFutures = {
    val futures: List[Future[Int]] = workLoad.map(heavyComputation)
    futures.foreach(_.foreach(println))

    // hard to obtain Future[List[Int]]]
  }

  // using traverse
  def traverseFuture = {
    val singleFuture: Future[List[Int]] = listTraverse.traverse(workLoad)(heavyComputation)
    singleFuture.foreach(println)
  }

  // traverse for IO
  def computeAsIO(string: String): IO[Int] = IO {
    Thread.sleep(1000)
    string.split(" ").length
  }.debug

  val ios: List[IO[Int]] = workLoad.map(computeAsIO)
  val singleIO: IO[List[Int]] = listTraverse.traverse(workLoad)(computeAsIO)

  // parallel traversal
  val parallelSingleIO: IO[List[Int]] = workLoad.parTraverse(computeAsIO)

  /**
   * Exercises
   */
  def sequence[A](listOfIOs: List[IO[A]]): IO[List[A]] =
    listTraverse.traverse(listOfIOs)(x => x)

  def sequence_v2[F[_] : Traverse, A](fOfIos: F[IO[A]]): IO[F[A]] =
    Traverse[F].traverse(fOfIos)(x => x)

  def parSequence[A](listOfIOs: List[IO[A]]): IO[List[A]] =
    listOfIOs.parTraverse(x => x)

  def parSequence_v2[F[_] : Traverse, A](fOfIos: F[IO[A]]): IO[F[A]] =
    fOfIos.parTraverse(x => x)

  // existing sequence API
  val singleIO_v2 = listTraverse.sequence(ios)

  // parallel sequencing
  val parallelSingleIO_v2 = ios.parSequence // parallel syntax package

  override def run: IO[Unit] = parallelSingleIO.void
}
