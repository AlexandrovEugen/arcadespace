package com.evgall.arcadespace.core.screens

import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.ecs.asset.TextureAsset
import com.evgall.arcadespace.core.ecs.asset.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger


private val LOG = logger<LoadingScreen>()

class LoadingScreen(boot: Boot) : ArcadeSpaceScreen(boot) {


    override fun show() {
        super.show()
        val before = System.currentTimeMillis()
        //queue assets loading
        val assetsRef = gdxArrayOf(
            TextureAsset.values().map { assetStorage.loadAsync(it.description) },
            TextureAtlasAsset.values().map { assetStorage.loadAsync(it.description) }
        ).flatten()
        //once assets are loaded -> change to GameScreen
        KtxAsync.launch {
            assetsRef.joinAll()
            LOG.debug { "Time for loading assets: ${System.currentTimeMillis() - before} ms" }
            assetsLoaded()
        }

        // TODO: 02.04.2021  setup UI
    }

    private fun assetsLoaded() {
        boot.addScreen(GameScreen(boot))
        boot.setScreen<GameScreen>()
        boot.removeScreen<LoadingScreen>()
        dispose()
    }
}
