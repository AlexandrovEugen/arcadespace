package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.PowerUpType
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import com.evgall.arcadespace.core.ecs.event.GameEvent
import com.evgall.arcadespace.core.ecs.event.GameEventListener
import com.evgall.arcadespace.core.ecs.event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.error
import ktx.log.logger
import kotlin.math.min


private val LOG: Logger = logger<RenderSystem>()

class RenderSystem(
    private val uiViewport: Viewport,
    private val batch: Batch,
    private val viewPort: Viewport,
    backgroundTexture: Texture,
    private val gameEventManager: GameEventManager
) : SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicsComponent::class).get(),
    compareBy { entity ->
        entity[TransformComponent.mapper]
    }
), GameEventListener {


    private val backgroundScrollingSpeed = Vector2(0.03f, 0.025f)

    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.CollectPowerUp::class, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.CollectPowerUp::class, this)
    }

    override fun update(deltaTime: Float) {

        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            background.run {
                backgroundScrollingSpeed.y = min(
                    -0.25f,
                    backgroundScrollingSpeed.y + deltaTime * (1f / 10f)
                )
                //render background
                scroll(
                    backgroundScrollingSpeed.x * deltaTime,
                    backgroundScrollingSpeed.y * deltaTime
                )
                draw(batch)
            }
        }

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

    override fun onEvent(event: GameEvent) {
        val powerUpEvent = event as GameEvent.CollectPowerUp
        if (powerUpEvent.type == PowerUpType.SPEED_1) {
            backgroundScrollingSpeed.y -= 0.25f
        } else if (powerUpEvent.type == PowerUpType.SPEED_2) {
            backgroundScrollingSpeed.y -= 0.5f
        }
    }
}
