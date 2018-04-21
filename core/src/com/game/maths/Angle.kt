package com.game.maths

class Angle(rad: Double = 0.0) {

    var radians = clamp(rad)

    fun copy(): Angle = Angle(radians)

    fun set(angle: Angle) { radians = clamp(angle.radians) }

    fun set(angle: Double) { radians = clamp(angle) }

    fun sin(): Double = Math.sin(radians)

    fun cos(): Double = Math.cos(radians)

    fun tan(): Double = Math.tan(radians)

    fun deg(): Double = radians * Const.TO_DEGREES

    private fun clamp(toClamp: Double): Double {
        var clamped = toClamp
        while(clamped < 0) { clamped += Const.TAU }
        while(clamped >= Const.TAU) { clamped -= Const.TAU }
        return clamped
    }

    fun clamp(): Angle {
        radians = clamp(radians)
        return this
    }

    fun quickestDirectionTo(other: Angle): Int {
        val clockwiseAngle = clamp(other.radians - radians)
        val anticlockwiseAngle = clamp(radians - other.radians)
        return if(anticlockwiseAngle < clockwiseAngle) -1 else 1
    }

    operator fun plus(rad: Double): Angle = Angle(clamp(radians + rad))

    operator fun plus(angle: Angle): Angle = this + angle.radians

    operator fun plusAssign(rad: Double) { radians = clamp(radians + rad) }

    operator fun plusAssign(angle: Angle) { radians = clamp(radians + angle.radians) }

    operator fun minus(rad: Double): Angle = Angle(clamp(radians - rad))

    operator fun minus(angle: Angle): Angle = this - angle.radians

    operator fun minusAssign(rad: Double) { radians = clamp(radians - rad) }

    operator fun minusAssign(angle: Angle) { radians = clamp(radians - angle.radians) }

    operator fun unaryMinus(): Angle = Angle(-radians)

    operator fun compareTo(angle: Angle): Int = if(radians == angle.radians) 0 else if(radians < angle.radians) - 1 else 1
}