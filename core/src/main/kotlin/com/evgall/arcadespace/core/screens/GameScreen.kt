package com.evgall.arcadespace.core.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.UNIT_SCALE
import com.evgall.arcadespace.core.V_WIDTH
import com.evgall.arcadespace.core.ecs.asset.MusicAsset
import com.evgall.arcadespace.core.ecs.component.*
import com.evgall.arcadespace.core.ecs.system.DAMAGE_AREA_HEIGHT
import com.evgall.arcadespace.core.event.GameEvent
import com.evgall.arcadespace.core.event.GameEventListener
import com.evgall.arcadespace.core.ui.GameUI
import ktx.actors.plusAssign
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import kotlin.math.min

private val LOG: Logger = logger<GameScreen>()

private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(
    boot: Boot,
    val engine: Engine = boot.engine
) : ArcadeSpaceScreen(boot), GameEventListener {

    private val ui = GameUI()

    override fun show() {
        LOG.debug { "Game screen has been shown" }

        gameEventManager.addListener(GameEvent.PlayerDeath::class, this)
        gameEventManager.addListener(GameEvent.PlayerHit::class, this)
        gameEventManager.addListener(GameEvent.CollectPowerUp::class, this)
        gameEventManager.addListener(GameEvent.PlayerBlock::class, this)
        gameEventManager.addListener(GameEvent.PlayerMove::class, this)
        audioService.play(MusicAsset.GAME)

        spawnPlayer()

        engine.entity {
            with<TransformComponent> {
                size.set(
                    V_WIDTH.toFloat(),
                    DAMAGE_AREA_HEIGHT
                )
            }
            with<AnimationComponent> {
                type = AnimationType.ARCADE_SPACE
            }
            with<GraphicsComponent>()
        }
        setUpUI()
    }

    override fun setUpUI() {
        stage += ui
    }

    override fun hide() {
        super.hide()
        stage.clear()
        gameEventManager.removeListener(GameEvent.PlayerDeath::class, this)
        gameEventManager.removeListener(GameEvent.PlayerHit::class, this)
        gameEventManager.removeListener(GameEvent.CollectPowerUp::class, this)
        gameEventManager.removeListener(GameEvent.PlayerBlock::class, this)
        gameEventManager.removeListener(GameEvent.PlayerMove::class, this)
    }

    private fun spawnPlayer() {
        val player = engine.entity {
            with<TransformComponent> {
                setInitialPosition(4.5f, 8f, 1f)
            }
            with<MoveComponent>()
            with<GraphicsComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }

        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = player
                offset.set(1f * UNIT_SCALE, -8f * UNIT_SCALE)
            }
            with<GraphicsComponent>()
            with<AnimationComponent> {
                type = AnimationType.FIRE
            }
        }
    }

    override fun render(delta: Float) {
        (boot.batch as SpriteBatch).renderCalls = 0
        engine.update(min(MAX_DELTA_TIME, delta))
        audioService.update()
        stage.run {
            viewport.apply()
            act()
            draw()
        }
        LOG.debug {
            "Render calls: ${(boot.batch as SpriteBatch).renderCalls}"
        }
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayerDeath -> {
                LOG.debug { "Player has been died with a distance of ${event.distance}" }
                preferences.flush {
                    this["current-score"] = event.distance
                    if (this["high-score", 0f] < event.distance) {
                        LOG.debug { "Player has beaten previous record" }
                        this["high-score"] = event.distance
                    }
                }
                spawnPlayer()
                ui.updateLife(MAX_LIFE, MAX_LIFE)
            }
            is GameEvent.PlayerHit -> {
                ui.updateLife(event.life, event.maxLife)
            }
            is GameEvent.CollectPowerUp -> {
                powerUp(event)
            }
            is GameEvent.PlayerBlock -> {
                ui.updateShield(event.shield, event.maxShield)
            }
            is GameEvent.PlayerMove -> {
                ui.updateDistance(event.distance)
                ui.updateSpeed(event.speed)
            }
        }
    }

    private fun powerUp(event: GameEvent.CollectPowerUp) {
        event.player[PlayerComponent.mapper]?.let { player ->
            when (event.type) {
                PowerUpType.LIFE -> {
                    ui.updateLife(player.life, player.maxLife)
                }
                PowerUpType.SHIELD -> {
                    ui.updateShield(player.shield, player.maxShield)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }
}