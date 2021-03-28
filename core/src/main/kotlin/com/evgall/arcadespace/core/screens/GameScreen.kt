package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.ecs.component.FacingComponent
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.PlayerComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(boot: Boot) : ArcadeSpaceScreen(boot) {


    override fun show() {
        LOG.debug { "First screen has been shown" }

        engine.entity {
            with<TransformComponent> {
                position.set(4.5f, 8f, 0f)
            }
            with<GraphicsComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }
        engine.entity {
            with<TransformComponent> {
                position.set(1f, 1f, 0f)
            }
            with<GraphicsComponent>{
                setSpriteRegion(boot.graphicsAtlas.findRegion("ship_left"))
            }
        }
        engine.entity {
            with<TransformComponent> {
                position.set(8f, 1f, 0f)
            }
            with<GraphicsComponent>{
                setSpriteRegion(boot.graphicsAtlas.findRegion("ship_right"))
            }
        }
    }

    override fun render(delta: Float) {
        (boot.batch as SpriteBatch).renderCalls = 0
        engine.update(delta)
        LOG.debug { "Render calls: ${(boot.batch as SpriteBatch).renderCalls}" }
    }
}