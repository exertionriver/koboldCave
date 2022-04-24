package org.river.exertion.ai.internalFocus

interface IMission : IInternalFocusTypeInstance {

    var objectives : MutableList<IInternalFocus_>
    var urgency : Float
    var accomplishment : Float
}