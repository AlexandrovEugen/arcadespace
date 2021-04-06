package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.ecs.asset.ShaderProgramAsset
import com.evgall.arcadespace.core.ecs.asset.SoundAsset
import com.evgall.arcadespace.core.ecs.asset.TextureAsset
import com.evgall.arcadespace.core.ecs.asset.TextureAtlasAsset
import com.evgall.arcadespace.core.ui.LabelStyle
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*


private val LOG = logger<LoadingScreen>()

class LoadingScreen(boot: Boot) : ArcadeSpaceScreen(boot) {

    private lateinit var progressBar: Image
    private lateinit var touchToBeginLabel: Label


    override fun show() {
        super.show()
        val before = System.currentTimeMillis()
        //queue assets loading
        val assetsRef = gdxArrayOf(
            TextureAsset.values().map { assetStorage.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assetStorage.loadAsync(it.descriptor) },
            SoundAsset.values().map { assetStorage.loadAsync(it.descriptor) },
            ShaderProgramAsset.values().map { assetStorage.loadAsync(it.descriptor) }
        ).flatten()
        //once assets are loaded -> change to GameScreen
        KtxAsync.launch {
            assetsRef.joinAll()
            LOG.debug { "Time for loading assets: ${System.currentTimeMillis() - before} ms" }
            assetsLoaded()
        }

        setUpUI()
    }

    override fun setUpUI() {
        stage.actors {
            table {
                defaults().fillX().expandX()
                label("Loading screen", LabelStyle.GRADIENT.name) {
                    wrap = true
                    setAlignment(Align.center)
                }
                row()
                touchToBeginLabel = label("Touch to begin", LabelStyle.DEFAULT.name) {
                    wrap = true
                    setAlignment(Align.center)
                    color.a = 0f
                }
                row()
                stack { cell ->
                    progressBar = image("life_bar") {
                        scaleX = 0f
                    }
                    label("Loading", LabelStyle.DEFAULT.name) {
                        setAlignment(Align.center)
                    }
                    cell.padLeft(5f).padRight(5f)
                }
                setFillParent(true)
                pack()
            }
        }
    }

    override fun hide() {
        stage.clear()
    }


    override fun render(delta: Float) {
        if (assetStorage.progress.isFinished &&
            Gdx.input.justTouched() &&
            boot.containsScreen<GameScreen>()
        ) {
            boot.setScreen<GameScreen>()
            boot.removeScreen<LoadingScreen>()
            dispose()
        }
        progressBar.scaleX = assetStorage.progress.percent
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    private fun assetsLoaded() {
        boot.addScreen(GameScreen(boot))
        touchToBeginLabel += forever(sequence(fadeIn(0.5f) + fadeOut(0.5f)))
    }
}
