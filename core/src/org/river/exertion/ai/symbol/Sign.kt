package org.river.exertion.ai.symbol

import org.river.exertion.ai.attribute.Trait
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.property.Quality
import org.river.exertion.btree.v0_1.Behavior

data class Sign(
        var noumenon: INoumenon?,
        var behavior: Behavior?,
        var characteristic: Trait<*>?,
        var quality: Quality<*>?,
        var facet: InternalFacetAttribute?,
//    var focus: InternalFocusAttribute?
)
