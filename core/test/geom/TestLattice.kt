package geom

import org.junit.jupiter.api.Test
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.RoundedLattice


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