package org.river.exertion.ai.internalFocus

data class InternalFocusInstance(override var tag : String, override var type : InternalFocusType, override var instance : IInternalFocusTypeInstance? = null) : IInternalFocus