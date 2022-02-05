package koboldCave

import org.junit.jupiter.api.Test
import org.river.exertion.Point
import org.river.exertion.koboldCave.lattice.ArrayLattice
import org.river.exertion.koboldCave.lattice.RoundedLattice
import org.river.exertion.koboldCave.leaf.Leaf
import java.time.LocalDateTime
import kotlin.random.Random


@ExperimentalUnsignedTypes
class TestLattice {

    @Test
    fun testArrayLattice() {
        val arrayLattice = ArrayLattice(topHeight = 3)
        println(arrayLattice)
        println("arrayLattice size : " + arrayLattice.getSet().size)
        for(setLattice in arrayLattice.getSet()) {
            println(setLattice)
        }
    }

    @Test
    fun testRoundedLattice() {
        val roundedLattice = RoundedLattice(topHeight = 3)
        println(roundedLattice)
        println("roundedLattice size : " + roundedLattice.getSet().size)
        for (setLattice in roundedLattice.getSet()) {
            println(setLattice)
        }
    }
}