package rock_the_JVM.yt.doobie

import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import doobie.implicits._

object DoobieDemo extends IOApp{

  case class Actor(id: Int, name: String)
  case class Movie(id: String, title: String, year: Int, actors: List[String], director: String)

  implicit class Debugger[A](io: IO[A]){
    def debug: IO[A] = io.map{a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:myimdb",
    "docker",
    "docker"
  )

  def findAllActorNames: IO[List[String]] = {
    val query = sql"select name from actors".query[String]
    val action: doobie.ConnectionIO[List[String]] = query.to[List]
    action.transact(xa)
  }



  override def run(args: List[String]): IO[ExitCode] = {
    findAllActorNames.debug.as(ExitCode.Success)
  }
}
