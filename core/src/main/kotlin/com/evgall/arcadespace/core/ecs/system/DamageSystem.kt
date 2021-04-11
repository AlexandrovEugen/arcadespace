package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.evgall.arcadespace.core.ecs.component.PlayerComponent
import com.evgall.arcadespace.core.ecs.component.RemoveComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import com.evgall.arcadespace.core.event.GameEvent
import com.evgall.arcadespace.core.event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

const val DAMAGE_AREA_HEIGHT = 2f
private const val DAMAGE_PER_SECOND = 25f
private const val DAMAGE_EXPLOSION_DURATION = 0.9f

class DamageSystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(
    allOf(PlayerComponent::class, TransformComponent::class)
        .exclude(RemoveComponent::class).get()
) {


    override fun processEntity(player: Entity, deltaTime: Float) {
        val transformComponent = player[TransformComponent.mapper]
        require(transformComponent != null) {
            "Entity |entity| must have Transform component. entity=$player"
        }
        val playerComponent = player[PlayerComponent.mapper]
        require(playerComponent != null) {
            "Entity |entity| must have Player component. entity=$player"
        }

        if (transformComponent.position.y <= DAMAGE_AREA_HEIGHT) {
            var damage = DAMAGE_PER_SECOND * deltaTime
            if (playerComponent.shield > 0f) {
                gameEventManager.dispatchEvent(GameEvent.PlayerBlock.apply {
                    this.shield = playerComponent.shield
                    this.maxShield = playerComponent.maxShield
                })
                val blockAmountOfDamage = playerComponent.shield
                playerComponent.shield = max(0f, playerComponent.shield - damage)
                damage -= blockAmountOfDamage

                if (damage <= 0f) {
                    //entire damage has been blocked
                    return
                }
            }

            playerComponent.life -= damage
            gameEventManager.dispatchEvent(GameEvent.PlayerHit.apply {
                this.player = player
                this.life = playerComponent.life
                this.maxLife = playerComponent.maxLife
            })
            if (playerComponent.life <= 0f) {
                gameEventManager.dispatchEvent(GameEvent.PlayerDeath.apply {
                    this.distance = playerComponent.distance
                })
                player.addComponent<RemoveComponent>(engine) {
                    delay = DAMAGE_EXPLOSION_DURATION
                }
            }
        }


    }
}