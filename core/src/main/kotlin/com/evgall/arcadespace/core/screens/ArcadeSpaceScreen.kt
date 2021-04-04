package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.viewport.Viewport
import com.evgall.arcadespace.core.AudioService
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.event.GameEventManager
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class ArcadeSpaceScreen(
    val boot: Boot,
    private val viewPort: Viewport = boot.viewPort,
    private val uiViewPort: Viewport = boot.uiViewport,
    val gameEventManager: GameEventManager = boot.gameEventManager,
    val assetStorage: AssetStorage = boot.assets,
    val audioService: AudioService = boot.audioService,
    val preferences: Preferences = boot.preferences
) : KtxScreen {


    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height, true)
        uiViewPort.update(width, height, true)
    }
}