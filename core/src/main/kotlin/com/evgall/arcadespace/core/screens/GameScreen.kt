package com.evgall.arcadespace.core.screens

import com.badlogic.gdx.math.MathUtils
import com.evgall.arcadespace.core.Boot
import com.evgall.arcadespace.core.ecs.component.FacingComponent
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import com.evgall.arcadespace.core.ecs.component.PlayerComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.debug
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(boot: Boot) : ArcadeSpaceScreen(boot) {


    override fun show() {
        LOG.debug { "First screen has been shown" }

        engine.entity {
            with<TransformComponent> {
                position.set(MathUtils.random(0f, 9f), MathUtils.random(0f, 16f), 0f)
            }
            with<GraphicsComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }
}