package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.evgall.arcadespace.core.Boot
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(boot: Boot) : ArcadeSpaceScreen(boot) {
    private val ship = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val sprite: Sprite = Sprite(ship)

    override fun show() {
        LOG.debug { "Game screen has been shown" }
        sprite.setPosition(1f, 1f)
    }

    override fun render(delta: Float) {
        batch.use {
            sprite.draw(it)
        }
    }

    override fun dispose() {
        ship.dispose()
        batch.dispose()
    }
}