package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.evgall.arcadespace.core.ecs.component.FacingComponent
import com.evgall.arcadespace.core.ecs.component.FacingDirection
import com.evgall.arcadespace.core.ecs.component.PlayerComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get


private const val TOUCH_TOLERANCE_DISTANCE = 0.2f

class PlayerSystem(
    private val gameViewport: Viewport
) : IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class, FacingComponent::class).get()) {

    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facingComponent = entity[FacingComponent.mapper]
        require(facingComponent != null) {
            "Entity |entity| must have a Facing component.  entity=$entity"
        }
        val transformComponent = entity[TransformComponent.mapper]
        require(transformComponent != null) {
            "Entity |entity| must have a Transform component.  entity=$entity"
        }

        tmpVec.x = Gdx.input.x.toFloat()
        gameViewport.unproject(tmpVec)
        val diffX = tmpVec.x - transformComponent.position.x - transformComponent.size.x * 0.5
        facingComponent.direction = when {
            diffX  < -TOUCH_TOLERANCE_DISTANCE ->  FacingDirection.LEFT
            diffX  > TOUCH_TOLERANCE_DISTANCE ->  FacingDirection.RIGHT
            else  -> FacingDirection.DEFAULT
        }
    }
}