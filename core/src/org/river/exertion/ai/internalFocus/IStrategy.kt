package org.river.exertion.ai.internalFocus

interface IStrategy : IInternalFocusTypeInstance {

    var tactics : MutableSet<IInternalFocus>
    var conviction : Float
    var accomplishment : Float
}