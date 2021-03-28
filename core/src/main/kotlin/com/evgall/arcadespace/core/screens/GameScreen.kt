package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.ecs.component.*
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
            with<MoveComponent>()
            with<GraphicsComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }
    }

    override fun render(delta: Float) {
        (boot.batch as SpriteBatch).renderCalls = 0
        engine.update(delta)
        LOG.debug { "Render calls: ${(boot.batch as SpriteBatch).renderCalls}" }
    }
}