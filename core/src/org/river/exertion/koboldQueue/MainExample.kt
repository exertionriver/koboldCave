package org.river.exertion.koboldQueue

import org.river.exertion.ecs.template.Cave
import org.river.exertion.ecs.template.Kobold
import org.river.exertion.ecs.template.Register
import kotlin.time.ExperimentalTime

object MainExample {
//    RenderActionPlex.lateInit(containerRoot)

    @OptIn(ExperimentalTime::class)
    val globalReg = Register(kInstanceName = "testGlobalRegister")

//    instantiate { template = Cave; kInstanceName = "spookyCave"; register = globalReg }
//    instantiate { template = Kobold; kInstanceName = "gragg"; register = globalReg }
//    instantiate { template = Kobold; kInstanceName = "rrawwr"; register = globalReg }
}