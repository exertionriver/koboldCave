package org.river.exertion.btree.v0_1

enum class Behavior {

    ABIDE { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.01f }
        override fun description() = "abiding" }
    , SLEEP { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.01f }
        override fun description() = "sleeping" }
    , LOOK { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.01f }
        override fun description() = "looking" }
    , WATCH { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.01f }
        override fun description() = "watching" }
    , THINK { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.03f }
        override fun description() = "thinking" }
    , REFLECT { override fun updateObject(character : IBTCharacter) {character.mIntAnxiety += 0.01f}
        override fun description() = "abiding" }
    , APPROACH { override fun updateObject(character : IBTCharacter) { }
        override fun description() = "approaching" }
    , SCAN { override fun updateObject(character : IBTCharacter) { }
        override fun description() = "scanning" }
    , ASSESS_THREAT { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.05f }
        override fun description() = "assessing threat" }
    , ASSESS_OPPORTUNITY { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.05f }
        override fun description() = "assessing opportunity" }
    , RESOLVE { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.1f }
        override fun description() = "resolving encounter" }
        
    , WANDER { override fun updateObject(character : IBTCharacter) { }
        override fun description() = "wandering" }
    , BALTER { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.05f }
        override fun description() = "dancing clumsily" }
    , MUMBLE { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.03f }
        override fun description() = "mumbling" }
    , SCREECH { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.05f }
        override fun description() = "screeching" }

    , LIE_DOWN { override fun updateObject(character : IBTCharacter) { character.isStanding = false; character.isSitting = false; character.isLyingDown = true }
        override fun description() = "lying down" }
    , SIT { override fun updateObject(character : IBTCharacter) { character.isStanding = false; character.isSitting = true; character.isLyingDown = false }
        override fun description() = "sitting" }
    , STAND { override fun updateObject(character : IBTCharacter) { character.isStanding = true; character.isSitting = false; character.isLyingDown = false }
        override fun description() = "standing" }
    , NONE

    ;

    open fun updateObject(character: IBTCharacter) {}
    open fun description() : String = "not sure"
}