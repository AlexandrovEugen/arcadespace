package com.evgall.arcadespace.core.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.evgall.arcadespace.core.V_WIDTH_PIXELS
import ktx.actors.plusAssign
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import kotlin.math.roundToInt

private const val GAME_HUD_LARGE_AREA_WIDTH = 48f
private const val GAME_HUD_SMALL_AREA_WIDTH = 35f
private const val GAME_HUD_BORDER_SIZE_Y = 6f
private const val GAME_HUD_BORDER_SIZE_X = 7f
private const val LIFE_BAR_WIDTH = 23F
private const val LIFE_BAR_HEIGHT = 8f

class GameUI : Group() {

    private val hud = scene2d.image("game_hud") {

    }
    private val lifeBar = scene2d.image("life_bar") {
        width = LIFE_BAR_WIDTH
        height = LIFE_BAR_HEIGHT
    }
    private val shieldBar = scene2d.image("shield_bar") {
        width = LIFE_BAR_WIDTH
        height = LIFE_BAR_HEIGHT
        color.a = 0f
    }

    private val distanceLabel = scene2d.label("0", LabelStyle.DEFAULT.name) {
        width = GAME_HUD_LARGE_AREA_WIDTH
        setAlignment(Align.center)
    }

    private val speedLabel = scene2d.label("0",LabelStyle.DEFAULT.name){
        width = GAME_HUD_LARGE_AREA_WIDTH
        setAlignment(Align.center)
    }

    init {
        var gameHudX: Float
        var gameHudHeight: Float
        var gameHudWidth: Float
        this += scene2d.image("game_hud") {
            gameHudX = V_WIDTH_PIXELS * 0.5f - width * 0.5f
            gameHudHeight = height
            gameHudWidth = width
            x = gameHudX
        }

        this += speedLabel.apply {
            setPosition(
                gameHudX + gameHudWidth - GAME_HUD_BORDER_SIZE_X - GAME_HUD_SMALL_AREA_WIDTH,
                GAME_HUD_BORDER_SIZE_Y)
        }

        this += distanceLabel.apply {
            setPosition(
                gameHudX + gameHudWidth * 0.5f - GAME_HUD_LARGE_AREA_WIDTH * 0.5f,
                GAME_HUD_BORDER_SIZE_Y
            )
        }

        this += lifeBar.apply {
            setPosition(
                gameHudX + GAME_HUD_BORDER_SIZE_X,
                gameHudHeight * 0.5f - height * 0.5f
            )
        }
        this += shieldBar.apply {
            setPosition(
                gameHudX + GAME_HUD_BORDER_SIZE_X,
                gameHudHeight * 0.5f - height * 0.5f
            )

        }
    }


    fun updateLife(life: Float, maxLife: Float) {
        lifeBar.scaleX = MathUtils.clamp(life / maxLife, 0f, 1f)
    }

    fun updateShield(shield: Float, maxShield: Float) {
        shieldBar.color.a = MathUtils.clamp(shield / maxShield, 0f, 1f)
    }

    fun updateDistance(distance: Float){
        distanceLabel.run {
            text.setLength(0)
            text.append(MathUtils.clamp(distance, 0f, 9999f).roundToInt())
            invalidateHierarchy()
        }
    }

    fun updateSpeed(speed: Float){
        speedLabel.run {
            text.setLength(0)
            text.append(MathUtils.clamp(speed, -99f, 999f).roundToInt())
            invalidateHierarchy()
        }
    }
}