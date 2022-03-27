package org.river.exertion.btree.v0_1

import org.river.exertion.btree.v0_1.task_cond.*

enum class TaskEnum {

    Abide { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.01f } }
    , Sleep { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.01f } }
    , Look { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.01f } }
    , Watch { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.01f } }
    , Think { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.03f } }
    , Reflect { override fun updateObject(character : IBTCharacter) {character.mIntAnxiety += 0.01f} }
    , Approach { override fun updateObject(character : IBTCharacter) { } }
    , Scan { override fun updateObject(character : IBTCharacter) { } }
    , AssessThreat { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.05f } }
    , AssessOpportunity { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.05f } }
    , Resolve { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.1f } }
        
    , Wander { override fun updateObject(character : IBTCharacter) { } }
    , Balter { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.05f } }
    , Mumble { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.03f } }
    , Screech { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.05f } }

    , LieDown { override fun updateObject(character : IBTCharacter) { character.isStanding = false; character.isSitting = false; character.isLyingDown = true } }
    , Sit { override fun updateObject(character : IBTCharacter) { character.isStanding = false; character.isSitting = true; character.isLyingDown = false } }
    , Stand { override fun updateObject(character : IBTCharacter) { character.isStanding = true; character.isSitting = false; character.isLyingDown = false } }
    ;

    abstract fun updateObject(character: IBTCharacter)
}