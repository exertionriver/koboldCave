package org.river.exertion.ai.phenomena

import org.river.exertion.ecs.component.action.core.ActionType

class ExternalPhenomenaImpression {

    var type = ExternalPhenomenaType.NONE
    var actionType = ActionType.NONE
    var perceivedDistance = 0f
    var perceivedMagnitude = 0f
    var perceivedDirection = 0f //angle from perceiver

    var countdown = 0f //time left

}
