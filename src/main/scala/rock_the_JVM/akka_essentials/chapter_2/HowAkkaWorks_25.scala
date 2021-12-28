package rock_the_JVM.akka_essentials.chapter_2

/**
 * Things to concern:
 * - ordering of messages
 * - race conditions
 * - "asynchronous" meaning
 *
 * HOW AKKA WORKS?
 * - akka has a thread pool that it shares with actors,
 * - Actor structure:
 *  - message handler
 *  - message queue
 *  - thread pool (hundreds) LOTS of actors (1000000s) -> akka schedules actors for execution
 *
 *  Guarantees: only one thread operates on an actor at any time,
 *  - actors are effectively single-threaded,
 *  - no looks needed
 *
 *  Message delivery guarantees:
 *  - at most once delivery
 *  - for any sender-receiver pair, the message order is maintained
 */
