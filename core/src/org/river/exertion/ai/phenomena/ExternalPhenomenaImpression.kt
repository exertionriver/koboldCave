package org.river.exertion.ai.phenomena

import org.river.exertion.btree.v0_1.TaskType

class ExternalPhenomenaImpression {

    var type = ExternalPhenomenaType.NONE
    var taskType = TaskType.NONE
    var perceivedDistance = 0f
    var perceivedMagnitude = 0f
    var perceivedDirection = 0f //angle from perceiver

    var countdown = 0f //time left

}
