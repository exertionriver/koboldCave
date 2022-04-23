package org.river.exertion.ai.internalFocus

interface IQuest : IInternalFocusTypeInstance {

    var targets : MutableSet<IInternalFocus_>
    var conviction : Float
    var accomplishment : Float
}