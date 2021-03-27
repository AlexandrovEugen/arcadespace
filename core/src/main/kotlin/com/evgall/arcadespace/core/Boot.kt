package com.evgall.arcadespace.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import com.evgall.arcadespace.core.ecs.system.PlayerAnimationSystem
import com.evgall.arcadespace.core.ecs.system.PlayerSystem
import com.evgall.arcadespace.core.ecs.system.RenderSystem
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

    val viewPort = FitViewport(9f, 16f)

    val batch: Batch by lazy { SpriteBatch() }
    private val defaultRegion by lazy {
        TextureRegion(Texture(Gdx.files.internal("graphics/ship_base.png")))
    }
    private val leftRegion by lazy {
        TextureRegion(Texture(Gdx.files.internal("graphics/ship_left.png")))
    }
    private val rightRegion by lazy {
        TextureRegion(Texture(Gdx.files.internal("graphics/ship_right.png")))
    }

    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(PlayerSystem(viewPort))
            addSystem(
                PlayerAnimationSystem(defaultRegion, leftRegion, rightRegion)
            )
            addSystem(RenderSystem(batch, viewPort))
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
        defaultRegion.texture.dispose()
        leftRegion.texture.dispose()
        rightRegion.texture.dispose()
    }

}
