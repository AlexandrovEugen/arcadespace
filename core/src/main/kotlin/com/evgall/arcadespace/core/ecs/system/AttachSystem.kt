package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.evgall.arcadespace.core.ecs.component.AttachComponent
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.RemoveComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get

class AttachSystem :
    IteratingSystem(allOf(AttachComponent::class, TransformComponent::class, GraphicsComponent::class).get()),
    EntityListener {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attach = entity[AttachComponent.mapper]
        require(attach != null) {
            "Entity |entity| must have Attach component. entity=$entity"
        }
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {
            "Entity |entity| must have Transform component. entity=$entity"
        }
        val graphicsComponent = entity[GraphicsComponent.mapper]
        require(graphicsComponent != null) {
            "Entity |entity| must have a Graphic component.  entity=$entity"
        }

        //update position
        attach.entity[TransformComponent.mapper]?.let { attachComponent ->
            transform.interpolatedPosition.set(
                attachComponent.interpolatedPosition.x + attach.offset.x,
                attachComponent.interpolatedPosition.y + attach.offset.y,
                transform.position.z
            )
        }

        //update alpha value
        attach.entity[GraphicsComponent.mapper]?.let { attachGraphics ->
            graphicsComponent.sprite.setAlpha(attachGraphics.sprite.color.a)
        }
    }

    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        entities.forEach { entity ->
            entity[AttachComponent.mapper]?.let { attach ->
                if (attach.entity == removedEntity) {
                    entity.addComponent<RemoveComponent>(engine)
                }
            }
        }
    }
}