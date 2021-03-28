package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.evgall.arcadespace.core.ecs.component.PlayerComponent
import com.evgall.arcadespace.core.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem
import kotlin.math.min

private const val WINDOW_INFO_UPDATE_RATE = 0.25f

class DebugSystem : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_INFO_UPDATE_RATE) {


    init {
        setProcessing(true)
    }


    override fun processEntity(entity: Entity) {
        val transformComponent = entity[TransformComponent.mapper]
        require(transformComponent != null) {
            "Entity |entity| must have Transform component. entity=$entity"
        }
        val playerComponent = entity[PlayerComponent.mapper]
        require(playerComponent != null) {
            "Entity |entity| must have Player component. entity=$entity"
        }

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0) -> {
                //kill player
                transformComponent.position.y = 1f
                playerComponent.life = 1f
                playerComponent.shield = 0f
            }
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1) -> {
                //add shield
                playerComponent.shield = min(playerComponent.maxShield, playerComponent.shield + 25f)
            }
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2) -> {
                //disable movement
                engine.getSystem<MoveSystem>().setProcessing(false)
            }
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3) -> {
                //enable movement
                engine.getSystem<MoveSystem>().setProcessing(true)
            }
        }

        Gdx.graphics
            .setTitle(
                "DEBUG position: ${transformComponent.position}," +
                        " lift:${playerComponent.life}," +
                        " shield: ${playerComponent.shield}"
            )
    }
}