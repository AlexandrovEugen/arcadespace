package com.evgall.arcadespace.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import com.evgall.arcadespace.core.ecs.system.*
import com.evgall.arcadespace.core.screens.ArcadeSpaceScreen
import com.evgall.arcadespace.core.screens.GameScreen
import ktx.app.KtxGame
import ktx.ashley.add
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger


private val LOG: Logger = logger<Boot>()
const val UNIT_SCALE = 1 / 16f
const val V_WIDTH = 9
const val V_HEIGHT = 16

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Boot : KtxGame<ArcadeSpaceScreen>() {

    val viewPort = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())

    val batch: Batch by lazy { SpriteBatch() }


    private val graphicsAtlas by lazy {
        TextureAtlas(Gdx.files.internal("graphics/graphics.atlas"))
    }

    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(PlayerSystem(viewPort))
            addSystem(MoveSystem())
            addSystem(DamageSystem())
            addSystem(
                PlayerAnimationSystem(
                    graphicsAtlas.findRegion("ship_base"),
                    graphicsAtlas.findRegion("ship_left"),
                    graphicsAtlas.findRegion("ship_right")
                )
            )
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(RenderSystem(batch, viewPort))
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        LOG.debug { "Create game instance" }
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()

        LOG.debug { "Sprites in batch: ${(batch as SpriteBatch).maxSpritesInBatch}" }
        batch.dispose()
        graphicsAtlas.dispose()
    }

}
