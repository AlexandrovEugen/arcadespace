package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.UNIT_SCALE
import com.evgall.arcadespace.core.V_HEIGHT
import com.evgall.arcadespace.core.V_WIDTH
import com.evgall.arcadespace.core.ecs.component.*
import com.evgall.arcadespace.core.ecs.system.DAMAGE_AREA_HEIGHT
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger
import kotlin.math.min

private val LOG: Logger = logger<GameScreen>()

private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(boot: Boot) : ArcadeSpaceScreen(boot) {


    override fun show() {
        LOG.debug { "First screen has been shown" }

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
            with<AttachComponent>(){
                entity = player
                offset.set(1f * UNIT_SCALE, -8f * UNIT_SCALE)
            }
            with<GraphicsComponent>()
            with<AnimationComponent>{
                type = AnimationType.FIRE
            }
        }

        engine.entity {
            with<TransformComponent>{
                size.set(
                    V_WIDTH.toFloat(),
                    DAMAGE_AREA_HEIGHT
                )
            }
            with<AnimationComponent>{
                type =AnimationType.ARCADE_SPACE
            }
            with<GraphicsComponent>()
        }
    }

    override fun render(delta: Float) {
        (boot.batch as SpriteBatch).renderCalls = 0
        engine.update(min(MAX_DELTA_TIME, delta))
        LOG.debug {
            "Render calls: ${(boot.batch as SpriteBatch).renderCalls}"
        }
    }
}