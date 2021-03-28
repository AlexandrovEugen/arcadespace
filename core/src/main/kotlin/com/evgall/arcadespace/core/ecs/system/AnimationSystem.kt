package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import com.evgall.arcadespace.core.ecs.component.Animation2D
import com.evgall.arcadespace.core.ecs.component.AnimationComponent
import com.evgall.arcadespace.core.ecs.component.AnimationType
import com.evgall.arcadespace.core.ecs.component.GraphicsComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import java.util.*

private val LOG = logger<AnimationSystem>()
private const val ERROR_ATLAS_KEY = "error"

class AnimationSystem(
    private val atlas: TextureAtlas
) : IteratingSystem(allOf(AnimationComponent::class, GraphicsComponent::class).get()), EntityListener {

    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val aniComp = entity[AnimationComponent.mapper]
        require(aniComp != null) {
            "Entity |entity| must have a Animation component.  entity=$entity"
        }
        val graphicsComponent = entity[GraphicsComponent.mapper]
        require(graphicsComponent != null) {
            "Entity |entity| must have a Graphic component.  entity=$entity"
        }

        if (aniComp.type  == AnimationType.NONE){
            LOG.error {
                "No type specified for animation component $aniComp for |entity| entity=$entity"
            }
            return
        }
        if (aniComp.type == aniComp.animation.type){
            //animation is correctly set -> update
            aniComp.stateTime += deltaTime
        } else {
            //change animation
            aniComp.stateTime = 0f
            aniComp.animation = getAnimation(aniComp.type)
        }
        val frame = aniComp.animation.getKeyFrame(aniComp.stateTime)
        graphicsComponent.setSpriteRegion(frame)
    }

    override fun entityAdded(entity: Entity) {
        entity[AnimationComponent.mapper]?.let { aniComp ->
            aniComp.animation = getAnimation(aniComp.type)
            val frame = aniComp.animation.getKeyFrame(aniComp.stateTime)
            entity[GraphicsComponent.mapper]?.setSpriteRegion(frame)
        }
    }

    private fun getAnimation(type: AnimationType): Animation2D {
        var animation2D = animationCache[type]
        if (animation2D == null) {
            var regions = atlas.findRegions(type.atlasKey)
            if (regions.isEmpty) {
                LOG.error {
                    "No regions has been found for ${type.atlasKey}"
                }
                regions = atlas.findRegions(ERROR_ATLAS_KEY)
                if (regions.isEmpty) throw GdxRuntimeException("There is no error in the atlas")

            } else {
                LOG.debug {
                    "Adding animation of type $type with ${regions.size} regions"
                }
            }
            animation2D = Animation2D(type, regions, type.playMode, type.seedRate)
            animationCache[type] = animation2D
        }
        return animation2D
    }

    override fun entityRemoved(entity: Entity) = Unit
}