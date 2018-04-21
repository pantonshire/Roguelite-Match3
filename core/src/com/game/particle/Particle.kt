package com.game.particle

import com.game.graphics.GameCanvas
import com.game.maths.Vector

abstract class Particle(val position: Vector, val velocity: Vector) {

    var ticksExisted = 0

    fun update() {
        position += velocity
        ++ticksExisted
    }

    abstract fun shouldRemove(): Boolean

    abstract fun draw(canvas: GameCanvas)
}