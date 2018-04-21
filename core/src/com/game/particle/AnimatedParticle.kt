package com.game.particle

import com.game.graphics.Animation
import com.game.graphics.AnimationSequence
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Vector

class AnimatedParticle(position: Vector, velocity: Vector, texture: String, vararg sequences: AnimationSequence): Particle(position, velocity) {

    val animation: Animation = Animation(Textures.get(texture), *sequences)

    override fun shouldRemove(): Boolean = animation.isFinished()

    override fun draw(canvas: GameCanvas) {
        animation.updateAnimation()
        animation.draw(canvas, position)
    }
}