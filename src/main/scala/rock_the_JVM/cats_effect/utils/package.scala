package rock_the_JVM.cats_effect

import cats.effect.IO

package object utils {
  implicit class DebugWrapper[A](io: IO[A]) {
    def debug: IO[A] = for {
      a <- io
      t = Thread.currentThread().getName
      _ = println(s"[$t] $a")
    } yield a
  }
}
