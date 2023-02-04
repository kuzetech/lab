package com.kuze.bigdata.study.streaming.udsource

class SystemClock {

  val minPollTime = 25L

  /**
    * @return the same time (milliseconds since the epoch)
    *         as is reported by `System.currentTimeMillis()`
    */
  def getTimeMillis(): Long = System.currentTimeMillis()

  /**
    * @return value reported by `System.nanoTime()`.
    */
  def nanoTime(): Long = System.nanoTime()

  /**
    * @param targetTime block until the current time is at least this value
    * @return current system time when wait has completed
    */
  def waitTillTime(targetTime: Long): Long = {
    var currentTime = System.currentTimeMillis()

    var waitTime = targetTime - currentTime
    if (waitTime <= 0) {
      return currentTime
    }

    val pollTime = math.max(waitTime / 10.0, minPollTime).toLong

    while (true) {
      currentTime = System.currentTimeMillis()
      waitTime = targetTime - currentTime
      if (waitTime <= 0) {
        return currentTime
      }
      val sleepTime = math.min(waitTime, pollTime)
      Thread.sleep(sleepTime)
    }
    -1
  }
}
