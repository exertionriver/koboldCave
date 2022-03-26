package org.river.exertion.ai

class Knowable {

    enum class KnowableGranularity {
        OTHER { override fun getSignatureId(signature: Signature) = "other" }
        , ENTITY_TYPE { override fun getSignatureId(signature: Signature) = signature.type }
        , ENTITY_GROUP { override fun getSignatureId(signature: Signature) = signature.group }
        , ENTITY_INDIVIDUAL { override fun getSignatureId(signature: Signature) = signature.individual }
        , NONE
        ;
        open fun getSignatureId(signature: Signature) = "none"
    }

    enum class KnowableSource {
        EXPERIENCE
        , NONE
    }

    var granularity = KnowableGranularity.NONE
    var source = KnowableSource.NONE

    var signature = Signature.empty()
    var trust = 0f

    fun getSignatureId(granularity : KnowableGranularity) = granularity.getSignatureId(signature)
}