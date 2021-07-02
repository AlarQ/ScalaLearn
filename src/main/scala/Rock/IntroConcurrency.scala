package Rock

import java.util.concurrent.Executors

case object IntroConcurrency extends App {

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running thread...")
  })

  // creating JVM thread
  aThread.start() // gives the signal to start a JVM thread
  // create a JVM thread => OS thread
  aThread.join() // block current thread until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))

  threadHello.start()
  threadGoodbye.start()

  // executors and thread pools - way to reuse threads
  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("Something in the thread pool"))
  pool.execute(() => {
    Thread.sleep(1000)
    println("done after one second")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(2000)
    println("done after two seconds")
  })

  pool.shutdown()
  //  pool.execute(()=> println("should not appear")) - throws an exception in calling thread
  //  pool.shutdownNow()
  println(pool.isShutdown)

  // --------------------------------------------------------------------------------------------
  // CONCURRENCY PROBLEMS
  def runInBarrel = {
    var x = 0

    val thread1 = new Thread(() =>
      x = 1
    )

    val thread2 = new Thread(() =>
      x = 1
    )
    thread1.start()
    thread2.start()
    println(x)
  }

  // race condition - two threads fighting for access to variable x, most times impossible to do that
  for (_ <- 1 to 100) runInBarrel


  // method with thread -- creates --> thread -- creates --> thread
  def createThreads = {

    def loop(count: Int):Unit = {
      val thread =  new Thread(() => {
        println("Hello from thread #" + count)
        println(Thread.currentThread().getId)
        if (count > 1) loop(count - 1) else 0
      })
      thread.start()
      thread.join()
    }

    loop(50)
  }

  createThreads
}
