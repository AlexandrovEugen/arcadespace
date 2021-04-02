package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.evgall.arcadespace.core.V_HEIGHT
import com.evgall.arcadespace.core.V_WIDTH
import com.evgall.arcadespace.core.ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

private const val UPDATE_RATE = 1 / 25f
private const val HOR_ACCELERATION = 16.5f
private const val VER_ACCELERATION = 2.25f
private const val MAX_VER_NEG_PLAYER_SPEED = 0.75f
private const val MAX_VER_POS_PLAYER_SPEED = 5f
private const val MAX_HOR_SPEED = 5.5f

class MoveSystem : IteratingSystem(
    allOf(TransformComponent::class, MoveComponent::class)
        .exclude(RemoveComponent::class)
        .get()
) {

    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= UPDATE_RATE) {
            accumulator -= UPDATE_RATE
            entities.forEach { entity ->
                entity[TransformComponent.mapper]?.let { transform ->
                    transform.prevPosition.set(transform.position)
                }
            }
            super.update(UPDATE_RATE)
        }

        val alpha = accumulator / UPDATE_RATE
        entities.forEach { entity ->
            entity[TransformComponent.mapper]?.let { transform ->
                transform.interpolatedPosition
                    .set(
                        MathUtils.lerp(
                            transform.prevPosition.x,
                            transform.position.x,
                            alpha
                        ),
                        MathUtils
                            .lerp(
                                transform.prevPosition.y,
                                transform.position.y,
                                alpha
                            ),
                        transform.position.z
                    )
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.mapper]
        require(transformComponent != null) {
            "Entity |entity| must have Transform component. entity=$entity"
        }
        val moveComponent = entity[MoveComponent.mapper]
        require(moveComponent != null) {
            "Entity |entity| must have Move component. entity=$entity"
        }

        val playerComponent = entity[PlayerComponent.mapper]
        if (playerComponent != null) {
            //player movement
            entity[FacingComponent.mapper]?.let { facingComponent ->
                movePlayer(transformComponent, moveComponent, facingComponent, deltaTime)
            }
        } else {
            //other movement like powerups
            moveEntity(transformComponent, moveComponent, deltaTime)
        }
    }

    private fun movePlayer(
        transformComponent: TransformComponent,
        moveComponent: MoveComponent,
        facingComponent: FacingComponent,
        deltaTime: Float
    ) {
        //update horizontal speed
        moveComponent.speed.x = when (facingComponent.direction) {
            FacingDirection.LEFT -> min(0f, moveComponent.speed.x - HOR_ACCELERATION * deltaTime)
            FacingDirection.RIGHT -> max(0f, moveComponent.speed.x + HOR_ACCELERATION * deltaTime)
            else -> 0f
        }

        moveComponent.speed.x = MathUtils.clamp(moveComponent.speed.x, -MAX_HOR_SPEED, MAX_HOR_SPEED)
        //update vertical speed
        moveComponent.speed.y = MathUtils.clamp(
            moveComponent.speed.y - VER_ACCELERATION * deltaTime,
            -MAX_VER_NEG_PLAYER_SPEED, MAX_VER_POS_PLAYER_SPEED
        )
        moveEntity(transformComponent, moveComponent, deltaTime)
    }

    private fun moveEntity(transformComponent: TransformComponent, moveComponent: MoveComponent, deltaTime: Float) {
        transformComponent.position.x = MathUtils
            .clamp(
                transformComponent.position.x + moveComponent.speed.x * deltaTime,
                0f, V_WIDTH - transformComponent.size.x
            )
        transformComponent.position.y = MathUtils
            .clamp(
                transformComponent.position.y + moveComponent.speed.y * deltaTime,
                1f, V_HEIGHT + 1f - transformComponent.size.y
            )
    }
}