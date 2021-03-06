package com.evgall.arcadespace.core

import com.badlogic.gdx.Game
import com.evgall.arcadespace.core.FirstScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Boot : Game() {
    override fun create() {
        setScreen(FirstScreen())
    }
}