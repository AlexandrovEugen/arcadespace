package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.UNIT_SCALE
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(boot: Boot) : ArcadeSpaceScreen(boot) {
    private val viewPort = FitViewport(9f, 16f)
    private val ship = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val sprite: Sprite = Sprite(ship).apply {
        setSize(9 * UNIT_SCALE, 10 * UNIT_SCALE)
    }

    override fun show() {
        LOG.debug { "Game screen has been shown" }
        sprite.setPosition(1f, 1f)
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height, true)
    }

    override fun render(delta: Float) {
        batch.use(viewPort.camera.combined) {
            sprite.draw(it)
        }
    }

    override fun dispose() {
        ship.dispose()
        batch.dispose()
    }
}