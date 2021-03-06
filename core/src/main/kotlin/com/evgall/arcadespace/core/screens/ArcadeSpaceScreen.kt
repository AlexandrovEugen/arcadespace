package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.graphics.g2d.Batch
import com.evgall.arcadespace.core.Boot
import ktx.app.KtxScreen

abstract class ArcadeSpaceScreen(
    private val boot: Boot,
    val batch: Batch = boot.batch
) : KtxScreen