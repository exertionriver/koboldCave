package org.river.exertion.koboldQueue.time

import com.badlogic.gdx.utils.Timer

class Timer(val initTimer : Timer = Timer.instance() ) {

    fun getMillisecondsElapsed() : Int = initTimer.hashCode()//DateTime.now().unixMillis - initTimer.unixMillis).toInt()

    override fun toString() = "org.river.exertion.koboldQueue.time.Timer(${initTimer}) : ${getMillisecondsElapsed()}"
}
