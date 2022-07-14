package org.river.exertion.ai.internalFacet

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.manifest.IManifest
import org.river.exertion.ai.messaging.FacetMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.entity.IEntity
import kotlin.math.roundToInt

class InternalFacetAttributesState(val entity : Telegraph, var internalFacetAttributes: Set<InternalFacetAttribute>) : Telegraph  {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_CONDITION.id())
    }

    var mIntAnxiety = 0f

    fun baseline() : MutableSet<InternalFacetInstance> {

        val returnBaselineSet = mutableSetOf<InternalFacetInstance>()

        internalFacetAttributes.forEach { internalFacetAttribute ->
            returnBaselineSet.add(internalFacetAttribute.arisenFacetInstance(mIntAnxiety))
        }

        return returnBaselineSet
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_CONDITION.id()) {
                this.mIntAnxiety = msg.extraInfo as Float
            }
        }
        return true
    }
}

