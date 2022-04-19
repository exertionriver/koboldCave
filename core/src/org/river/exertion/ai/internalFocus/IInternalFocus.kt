package org.river.exertion.ai.internalFocus

interface IInternalFocus {

    var tag : String
    var type : InternalFocusType
    var instance : IInternalFocusTypeInstance?
}