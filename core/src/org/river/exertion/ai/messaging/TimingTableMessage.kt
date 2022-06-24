package org.river.exertion.ai.messaging

data class TimingTableMessage(var timingType: TimingEntryType = TimingEntryType.CHARACTER
                              , var label : String? = null
                              , var value : Float? = null) {
    enum class TimingEntryType {
        RENDER,
        CHARACTER
    }
}