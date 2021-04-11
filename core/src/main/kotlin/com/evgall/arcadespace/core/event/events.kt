package com.evgall.arcadespace.core.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import com.evgall.arcadespace.core.ecs.component.PowerUpType
import ktx.collections.GdxSet
import ktx.collections.getOrPut
import kotlin.reflect.KClass


sealed class GameEvent {
    object PlayerDeath : GameEvent() {
        var distance = 0f

        override fun toString() = "PlayerDeath(distance =${distance})"
    }

    object CollectPowerUp : GameEvent() {
        lateinit var player: Entity
        var type = PowerUpType.NONE

        override fun toString() = "CollectPowerUp(player=$player, type=$type)"
    }

    object PlayerHit : GameEvent() {
        lateinit var player: Entity
        var life = 0f
        var maxLife = 0f


        override fun toString(): String = "PlayerHit(player=$player, life=$life, maxLife=$maxLife)"
    }

    object PlayerBlock : GameEvent() {
        var shield = 0f
        var maxShield = 0f

        override fun toString() = "PlayerBlock(shield=$shield,maxShield=$maxShield)"
    }

    object PlayerMove: GameEvent(){
        var speed = 0f
        var distance = 0f

        override fun toString() = "PlayerMove(speed=$speed,distance=$distance)"
    }
}


interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {

    private val listeners = ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>()


    fun addListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        val eventListeners = listeners.getOrPut(type) { GdxSet() }
        eventListeners.add(listener)
    }


    fun removeListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        listeners[type].remove(listener)
    }


    fun removeListener(listener: GameEventListener) {
        ObjectMap.Values(listeners).forEach {
            it.remove(listener)
        }
    }

    fun dispatchEvent(event: GameEvent) {
        listeners[event::class].forEach { it.onEvent(event) }
    }

}