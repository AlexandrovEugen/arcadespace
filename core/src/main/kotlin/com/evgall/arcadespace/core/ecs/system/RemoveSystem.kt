package com.evgall.arcadespace.core.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.evgall.arcadespace.core.ecs.component.RemoveComponent
import ktx.ashley.allOf
import ktx.ashley.get

class RemoveSystem : IteratingSystem(allOf(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val removeComponent = entity[RemoveComponent.mapper]
        require(removeComponent != null) {
            "Entity |entity| must have  a Remove component. entity=$entity"
        }
        removeComponent.delay -= deltaTime
        if (removeComponent.delay.compareTo(0f) <= 0) {
            engine.removeEntity(entity)
        }
    }
}