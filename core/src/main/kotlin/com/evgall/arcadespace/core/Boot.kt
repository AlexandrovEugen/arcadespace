package com.evgall.arcadespace.core

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evgall.arcadespace.core.screens.ArcadeSpaceScreen
import com.evgall.arcadespace.core.screens.GameScreen
import ktx.app.KtxGame
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger


private val LOG: Logger = logger<Boot>()
const val UNIT_SCALE = 1 / 16f

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Boot : KtxGame<ArcadeSpaceScreen>() {

    val batch: Batch by lazy { SpriteBatch() }


    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug { "Create game instance" }
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }
}