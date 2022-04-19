package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.*

interface ISymbology {

    var symbols : MutableSet<SymbolType>
    var internalFocuses : MutableSet<InternalFocusInstance>

//    fun belief(lambda : InternalFocusInstance.() -> Unit) = InternalFocusInstance(tag = "belief", type = InternalFocusType.BELIEF, instance = ).apply(lambda)

    fun belief(lambda : BeliefInstance.() -> Unit) = InternalFocusInstance(tag = "belief", type = InternalFocusType.BELIEF, instance = beliefInstance { }.apply(lambda))
    fun logic(lambda : LogicInstance.() -> Unit) = InternalFocusInstance(tag = "logic", type = InternalFocusType.LOGIC, instance = logicInstance { }.apply(lambda))
    fun need(lambda : NeedInstance.() -> Unit) = InternalFocusInstance(tag = "need", type = InternalFocusType.NEED, instance = needInstance { }.apply(lambda))
    fun want(lambda : WantInstance.() -> Unit) = InternalFocusInstance(tag = "want", type = InternalFocusType.WANT, instance = wantInstance { }.apply(lambda))

    fun target(lambda : TargetInstance.() -> Unit) = InternalFocusInstance(tag = "target", type = InternalFocusType.TARGET, instance = targetInstance { }.apply(lambda))

    fun beliefInstance(lambda : BeliefInstance.() -> Unit) = BeliefInstance().apply(lambda)
    fun logicInstance(lambda : LogicInstance.() -> Unit) = LogicInstance().apply(lambda)
    fun needInstance(lambda : NeedInstance.() -> Unit) = NeedInstance().apply(lambda)
    fun wantInstance(lambda : WantInstance.() -> Unit) = WantInstance().apply(lambda)

    fun targetInstance(lambda : TargetInstance.() -> Unit) = TargetInstance().apply(lambda)

    fun symbolInstance(lambda : SymbolInstance.() -> Unit) = SymbolInstance().apply(lambda)
}