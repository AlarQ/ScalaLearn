package faultTolerance

object ActorLifecycle extends App{
  /**
   * Actor can be:
   * - Started - create a new ActorRef with UUID at a given path
   * - Suspended - the actor ref will enqueue but NOT process more messages
   * - Resumed - the actor ref will continue processing more messages
   * - Restarted:
   *  -> suspend
   *  -> swap actor instance
   *    --> odlInstance call preRestart
   *    --> replace actor instance
   *    --> newInstance call postRestart
   *  -> resume
   * - stopped
   *  -> call postStop
   *  -> all watching actor receive Terminated(ref)
   *  -> actor stopping, another actor can be created at the same path, different UUID - different ActorRef
   * preStart, postStop methods are used for cleaning
   */
}
