package com.evgall.arcadespace.core.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.UNIT_SCALE
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(boot: Boot) : ArcadeSpaceScreen(boot) {
    private val playerTexture = Texture(Gdx.files.internal("graphics/ship_base.png"))

    override fun show() {
        LOG.debug { "First screen has been shown" }

        repeat(10) {
            engine.entity {
                with<TransformComponent> {
                    position.set(it.toFloat(), it.toFloat(), 0f)
                }
                with<GraphicsComponent> {
                    sprite.run {
                        setRegion(playerTexture)
                        setSize(texture.width * UNIT_SCALE, texture.height * UNIT_SCALE)
                        setOriginCenter()
                    }
                }
            }
        }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        playerTexture.dispose()
    }
}