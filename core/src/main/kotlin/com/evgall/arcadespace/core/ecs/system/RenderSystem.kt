package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.error
import ktx.log.logger


private val LOG: Logger = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val viewPort: Viewport
) : SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicsComponent::class).get(),
    compareBy { entity ->
        entity[TransformComponent.mapper]
    }
) {

    override fun update(deltaTime: Float) {
        forceSort()
        viewPort.apply()
        batch.use(viewPort.camera.combined) {
            super.update(deltaTime)
        }
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {
            "Entity |entity| must have a Transform component.  entity=$entity"
        }
        val graphicsComponent = entity[GraphicsComponent.mapper]
        require(graphicsComponent != null) {
            "Entity |entity| must have a Transform component.  entity=$entity"
        }

        if (graphicsComponent.sprite.texture == null) {
            LOG.error {
                "Entity has no texture for rendering. entity=$entity"
            }
            return
        }

        graphicsComponent.sprite.run {
            rotation = transform.rotationDeg
            setBounds(
                transform.interpolatedPosition.x,
                transform.interpolatedPosition.y,
                transform.size.x,
                transform.size.y
            )
            draw(batch)
        }

    }
}
