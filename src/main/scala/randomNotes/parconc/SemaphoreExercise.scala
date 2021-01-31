package randomNotes.parconc

object SemaphoreExercise {

  /**
   * == TODO 1 ==
   *
   * ResourceGuard to klasa pozwalająca uzyskać dostęp do pewnego zasobu A jednocześnie dla
   * N wątków. Próba użycia metody execute przez kolejne wątki zostanie zablokowana i takie
   * wątki będą musiały poczekać aż pozostałe skończą swoją pracę.
   *
   * Jest to więc wyspecjalizowany rodzaj semafora.
   *
   * Zaimplementuj metodę execute. Możliwe że będziesz musiał dodać dodatkowe pola w klasie
   * ResourceGuard.
   *
   * Postaraj się skorzystać z synchronized/wait/notifyAll.
   *
   * Implementację możesz przetestować za pomocą testu w ResourceGuardSuite.
   *
   */
  class ResourceGuard[A](a: A, maxThreads: Int) {
    require(maxThreads > 0)
    var sem: Int = maxThreads

    def execute(f: A => Unit): Unit = {
      synchronized {
        while (sem <= 0) {
          wait()
        }
        sem -= 1
      }
      try {
        f(a)
      } finally {
        synchronized {
          sem += 1
          notifyAll()
        }
      }
    }
  }

}
