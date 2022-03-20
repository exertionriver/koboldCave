package org.river.exertion.btree.v0_1

import org.river.exertion.btree.v0_1.task_cond.*

enum class TaskEnum {

    Abide { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.01f } }
    , Sleep { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.01f } }
    , Look { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.01f; character.mExtAnxiety += 0.03f } }
    , Watch { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.01f; character.mExtAnxiety += 0.01f } }
    , Think { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.03f; character.mExtAnxiety -= 0.01f } }
    , Reflect { override fun updateObject(character : IBTCharacter) {character.mIntAnxiety += 0.01f; character.mExtAnxiety -= 0.01f } }
    , Approach { override fun updateObject(character : IBTCharacter) { character.mExtAnxiety += 0.03f } }
    , Scan { override fun updateObject(character : IBTCharacter) { character.mExtAnxiety -= 0.05f } }
    , AssessThreat { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.05f; character.mExtAnxiety += 0.05f } }
    , AssessOpportunity { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety += 0.05f; character.mExtAnxiety += 0.05f } }
    , Resolve { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.1f; character.mExtAnxiety -= 0.1f } }
        
    , Wander { override fun updateObject(character : IBTCharacter) { character.mExtAnxiety -= 0.03f } }
    , Balter { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.05f; character.mExtAnxiety -= 0.01f } }
    , Mumble { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.03f; character.mExtAnxiety -= 0.01f } }
    , Screech { override fun updateObject(character : IBTCharacter) { character.mIntAnxiety -= 0.05f; character.mExtAnxiety -= 0.03f } }

    , LieDown { override fun updateObject(character : IBTCharacter) { character.isStandingUp = false; character.isSitting = false; character.isLyingDown = true } }
    , Sit { override fun updateObject(character : IBTCharacter) { character.isStandingUp = false; character.isSitting = true; character.isLyingDown = false } }
    , StandUp { override fun updateObject(character : IBTCharacter) { character.isStandingUp = true; character.isSitting = false; character.isLyingDown = false } }
    ;

    abstract fun updateObject(character: IBTCharacter)
}