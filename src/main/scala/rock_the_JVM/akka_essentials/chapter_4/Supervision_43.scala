package rock_the_JVM.akka_essentials.chapter_4

class  Supervision_43 {

  //TODO - uzupełnić kod
  /**
   * When actor fails
   * - suspends all children
   * - sends a special message to its parent
   *
   * The parent can decide:
   * - to resume the actor
   * - restart the actor - default
   * - stop the actor
   * - escalate and fail itself
   *
   * supervisor strategy can be overridden as a partial function exception => reaction (Resume, Restart, Escalate, Stop)
   */
}
