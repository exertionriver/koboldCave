package ai

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.ExternalPhenomenaInstance
import org.river.exertion.ai.ExternalPhenomenaType
import org.river.exertion.ai.InternalPhenomenaInstance
import org.river.exertion.ai.InternalPhenomenaType
import org.river.exertion.ai.attributes.KoboldAttributable
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestAttribute {

    val ka = KoboldAttributable()

    @Test
    fun testAttributableLists() {
        ka.attributables.forEach { attr ->
            attr.key.getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        ka.attributables.forEach { attr ->
            println ( attr.key.getDescriptionByOrder(0) )
            println ( attr.key.getValueByOrder(0) )
            println ( attr.key.getDescriptionByValue( attr.key.getValueByOrder(0)!! ) )
        }
    }

    @Test
    fun testAttributablesGetValue() {
        ka.attributables.forEach { attr ->
            println ( attr.key.getDescriptionByValue( attr.key.getValue() ) )
            println ( attr.key.getDescriptionByValue( attr.key.getValue() ) )
            println ( attr.key.getDescriptionByValue( attr.key.getValue() ) )
        }
    }

}