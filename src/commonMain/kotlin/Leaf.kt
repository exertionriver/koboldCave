import com.soywiz.korio.util.UUID
import kotlin.math.max
import kotlin.random.Random

@ExperimentalUnsignedTypes
open class Leaf(val uuid: UUID = UUID.randomUUID(Random.Default)
                , val initHeight : Int = 3
                , val parentLeafNode : Leaf? = null
            ) {

    open val childrenLeafNodes = MutableList(size = randChildSizeList(initHeight)) {
        if (initHeight == 0) null else Leaf(
            initHeight = initHeight - 1,
            parentLeafNode = this
        )
    }

    val childrenEmpty = childrenLeafNodes.isNullOrEmpty()

    fun getLeafNodeList() : List<Leaf> = listOf(this) + childrenLeafNodes.flatMap { it!!.getLeafNodeList() }

    fun getCurrentHeight() : Int =
        if (childrenLeafNodes == emptyLeaf().childrenLeafNodes) 0
        else (1 + childrenLeafNodes.map { it!!.getCurrentHeight() }.reduce { maxVal : Int, element -> max(maxVal, element) })

    override fun toString() = "Leaf($uuid) : ${initHeight}, ${childrenLeafNodes.size}"

    companion object {

        fun emptyLeaf() = Leaf(initHeight = 0)

        fun randChildSizeList(height : Int): Int {
            return when {
                (height > 2) -> ProbabilitySelect(
                    mapOf(
                        "1" to Probability(60, 0),
                        "2" to Probability(25, 0),
                        "3" to Probability(5, 0)
                    )
                ).getSelectedProbability()!!.toInt()
                (height > 0) -> ProbabilitySelect(
                    mapOf(
                        "1" to Probability(20, 0),
                        "2" to Probability(30, 0),
                        "3" to Probability(50, 0)
                    )
                ).getSelectedProbability()!!.toInt()
                else -> 0
            }
        }

    }

}