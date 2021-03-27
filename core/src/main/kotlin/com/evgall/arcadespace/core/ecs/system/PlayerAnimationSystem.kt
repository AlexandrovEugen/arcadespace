package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.evgall.arcadespace.core.ecs.component.FacingComponent
import com.evgall.arcadespace.core.ecs.component.FacingDirection
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
) : IteratingSystem(allOf(PlayerComponent::class, FacingComponent::class, GraphicsComponent::class).get()),
    EntityListener {


    private var lastDirection = FacingDirection.DEFAULT

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facingComponent = entity[FacingComponent.mapper]
        require(facingComponent != null) {
            "Entity |entity| must have a Facing component.  entity=$entity"
        }
        val graphicsComponent = entity[GraphicsComponent.mapper]
        require(graphicsComponent != null) {
            "Entity |entity| must have a Graphics component.  entity=$entity"
        }

        if (facingComponent.direction == lastDirection && graphicsComponent.sprite.texture != null) {
            //texture is already set and direction did not change -> do nothing
            return
        }
        lastDirection = facingComponent.direction
        val region = when (facingComponent.direction) {
            FacingDirection.LEFT -> leftRegion
            FacingDirection.RIGHT -> rightRegion
            else -> defaultRegion
        }
        graphicsComponent.setSpriteRegion(region)
    }

    override fun entityAdded(entity: Entity) {
        entity[GraphicsComponent.mapper]?.setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity?) = Unit


}