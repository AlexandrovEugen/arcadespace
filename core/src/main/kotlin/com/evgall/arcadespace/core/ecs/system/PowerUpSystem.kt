package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.evgall.arcadespace.core.V_WIDTH
import com.evgall.arcadespace.core.ecs.component.*
import ktx.ashley.*
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import kotlin.math.min


private val LOG = logger<PowerUpSystem>()
private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f
private const val POWER_UP_SPEED = -8.75f
private const val BOOST_1_SPEED_GAIN = 3f
private const val BOOST_2_SPEED_GAIN = 3.76f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f


private class SpawnPattern(
    type1: PowerUpType = PowerUpType.NONE,
    type2: PowerUpType = PowerUpType.NONE,
    type3: PowerUpType = PowerUpType.NONE,
    type4: PowerUpType = PowerUpType.NONE,
    type5: PowerUpType = PowerUpType.NONE,
    val types: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class PowerUpSystem :
    IteratingSystem(allOf(PowerUpComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    private val playerBoundRect = Rectangle()
    private val powerUpBoundRect = Rectangle()
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())
    }
    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(type1 = PowerUpType.SPEED_1, type3 = PowerUpType.SPEED_2, type5 = PowerUpType.LIFE),
        SpawnPattern(type1 = PowerUpType.LIFE, type2 = PowerUpType.SHIELD, type4 = PowerUpType.SPEED_2)
    )

    private val currentSpawnPattern = GdxArray<PowerUpType>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime < 0) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)
            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, spawnPatterns.size - 1)].types)
                LOG.debug { "Next pattern: $currentSpawnPattern" }
            }
            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType == PowerUpType.NONE){
                return
            }
            spawnPowerUp(powerUpType, 1f * MathUtils.random(0, V_WIDTH -1), 16f)
        }

    }

    private fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(x, y, 0f)
            }
            with<PowerUpComponent>{
                type = powerUpType
            }
            with<AnimationComponent>{
                type = powerUpType.animationType
            }
            with<GraphicsComponent>()
            with<MoveComponent>{
                speed.y = POWER_UP_SPEED
            }

        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {
            "Entity |entity| must have Transform component. entity=$entity"
        }

        if (transform.position.y <= 1f){
            entity.addComponent<RemoveComponent>(engine)
            return
        }
        else {
            powerUpBoundRect.set(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
            playerEntities.forEach{ player ->
                player[TransformComponent.mapper]?.let { trComp ->
                    playerBoundRect.set(
                        trComp.position.x,
                        trComp.position.y,
                        trComp.size.x,
                        trComp.size.y
                    )
                    if (playerBoundRect.overlaps(powerUpBoundRect)){
                        collectPowerUp(player, entity)
                    }
                }
            }
        }
    }

    private fun collectPowerUp(player: Entity, powerUp: Entity) {
        val powerUpComponent = powerUp[PowerUpComponent.mapper]
        require(powerUpComponent !=null){
            "Entity |entity| mast have Power Up Component. entity =$powerUp"
        }
        LOG.debug {
            "Picking up power up of type ${powerUpComponent.type}"
        }

        when(powerUpComponent.type){
            PowerUpType.SPEED_1 -> {
                player[MoveComponent.mapper]?.let { moveComponent ->
                    moveComponent.speed.y += BOOST_1_SPEED_GAIN
                }
            }
            PowerUpType.SPEED_2 -> {
                player[MoveComponent.mapper]?.let { moveComponent ->
                    moveComponent.speed.y += BOOST_2_SPEED_GAIN
                }
            }
            PowerUpType.LIFE -> {
                player[PlayerComponent.mapper]?.let { playerComponent ->
                    playerComponent.life = min(playerComponent.maxLife, playerComponent.life + LIFE_GAIN)
                }
            }
            PowerUpType.SHIELD -> {
                player[PlayerComponent.mapper]?.let { playerComponent ->
                    playerComponent.shield = min(playerComponent.maxShield, playerComponent.shield + SHIELD_GAIN)
                }
            }
            else -> {
                LOG.error { "Unsupported power up of type ${powerUpComponent.type}" }
            }
        }

        powerUp.addComponent<RemoveComponent>(engine)
    }
}