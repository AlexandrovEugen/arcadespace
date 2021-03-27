package com.evgall.arcadespace.core.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.evgall.arcadespace.core.Boot
import ktx.app.KtxScreen

abstract class ArcadeSpaceScreen(
    private val boot: Boot,
    val batch: Batch = boot.batch,
    private val viewPort: Viewport = boot.viewPort,
    val engine: Engine = boot.engine
) : KtxScreen {


    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height, true)
    }
}