package org.river.exertion.geom.room

class RoomAttributes {

    enum class RoomSize {
        SMALL { override fun getNodeRoomHeight() = 3 }
        , MEDIUM { override fun getNodeRoomHeight() = 4 }
        , LARGE { override fun getNodeRoomHeight() = 5 }
        ; abstract fun getNodeRoomHeight() : Int }

    var roomSize : RoomSize = RoomSize.SMALL

}