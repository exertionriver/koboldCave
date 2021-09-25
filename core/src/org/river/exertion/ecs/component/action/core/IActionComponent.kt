package org.river.exertion.ecs.component.action.core

import org.river.exertion.ActionDescription

interface IActionComponent {

    val label : String
    val description : ActionDescription
    var type : ActionType
    var priority : ActionPriority
    var state : ActionState

    var plexSlotsFilled : Int
    var plexSlotsRequired : Int
    var maxParallel : Int

    val momentsToPrepare : Int
    val momentsToExecute : Int
    val momentsToRecover : Int
}