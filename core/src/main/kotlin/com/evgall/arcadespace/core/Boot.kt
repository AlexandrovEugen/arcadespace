package com.evgall.arcadespace.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.evgall.arcadespace.core.ecs.asset.TextureAsset
import com.evgall.arcadespace.core.ecs.asset.TextureAtlasAsset
import com.evgall.arcadespace.core.event.GameEventManager
import com.evgall.arcadespace.core.ecs.system.*
import com.evgall.arcadespace.core.screens.ArcadeSpaceScreen
import com.evgall.arcadespace.core.screens.LoadingScreen
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger


private val LOG: Logger = logger<Boot>()
const val UNIT_SCALE = 1 / 16f
const val V_WIDTH = 9
const val V_HEIGHT = 16
const val V_WIDTH_PIXELS = 134
const val V_HEIGHT_PIXELS = 240


/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Boot : KtxGame<ArcadeSpaceScreen>() {

    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
    val viewPort = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    val batch: Batch by lazy { SpriteBatch() }
    val gameEventManager = GameEventManager()
    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }

    val engine: Engine by lazy {
        val graphicsAtlas = assets[TextureAtlasAsset.GAME_GRAPHICS.description]

        PooledEngine().apply {
            addSystem(PlayerSystem(viewPort))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager))
            addSystem(DamageSystem(gameEventManager))
            addSystem(CameraShakeSystem(gameEventManager, viewPort.camera))
            addSystem(
                PlayerAnimationSystem(
                    graphicsAtlas.findRegion("ship_base"),
                    graphicsAtlas.findRegion("ship_left"),
                    graphicsAtlas.findRegion("ship_right")
                )
            )
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(
                RenderSystem(
                    uiViewport,
                    batch,
                    viewPort,
                    assets[TextureAsset.BACKGROUND.description],
                    gameEventManager
                )
            )
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug { "Create game instance" }
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }

    override fun dispose() {
        super.dispose()

        LOG.debug { "Sprites in batch: ${(batch as SpriteBatch).maxSpritesInBatch}" }
        batch.dispose()
        assets.dispose()
    }

}
