package org.river.exertion.koboldQueue.time

class Moment(val milliseconds : Long) {

    companion object {
        val Immediate = Moment(0L)
    }
}

