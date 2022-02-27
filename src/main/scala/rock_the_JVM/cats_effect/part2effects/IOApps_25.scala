package rock_the_JVM.cats_effect.part2effects

import cats.effect.{ExitCode, IO, IOApp}
import rock_the_JVM.cats_effect.part2effects.IOApps.program

import scala.io.StdIn

object IOApps{
  val program = for{
    line <- IO(StdIn.readLine())
    _ <- IO(println(s"Printing...: $line"))
  } yield  ()
}


object FirstCatsEffectApp extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = program.as(ExitCode.Success) // program.map(_ => ExitCode.Success)
}

object MySimpleApp extends IOApp.Simple {
  override def run: IO[Unit] = program
}