package org.river.exertion.ai.condition

import org.river.exertion.ai.symbol.SymbolType

class ConditionInstance : ICondition {

    override var mLife = 1f
    override var mLifeRegen = 0.05f

    override var mIntAnxiety = .2f
    override var mAwake = .6f

    override var mTiredness = .2f
    override var mExhaustion = .2f
    override var mHunger = .2f
    override var mThirst = .2f

    companion object {

        fun ConditionInstance.symbolType() : SymbolType {
            return when {
                ( (mLife > .7) && (mIntAnxiety < .4) ) -> SymbolType.GOOD_HEALTH
                else -> SymbolType.UNKNOWN_HEALTH
            }
        }
    }
}