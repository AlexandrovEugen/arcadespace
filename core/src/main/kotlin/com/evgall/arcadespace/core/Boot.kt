package com.evgall.arcadespace.core

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.Logger
import ktx.log.debug
import ktx.log.info
import ktx.log.logger


private val LOG: Logger = logger<Boot>()

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Boot : KtxGame<KtxScreen>() {
    override fun create() {
        LOG.debug { "Create game instance" }
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}