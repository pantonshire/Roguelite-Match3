package com.game.maths

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import java.awt.Point

class Vector(var x: Double = 0.0, var y: Double = 0.0) {

    constructor(gdxVector: Vector2): this(gdxVector.x.toDouble(), gdxVector.y.toDouble())

    constructor(gdxVector: Vector3): this(gdxVector.x.toDouble(), gdxVector.y.toDouble())

    fun copy(): Vector = Vector(x, y)

    fun toVector2(): Vector2 = Vector2(x.toFloat(), y.toFloat())

    fun toVector3(): Vector3 = Vector3(x.toFloat(), y.toFloat(), 0f)

    fun round(): Point = Point(Math.round(x).toInt(), Math.round(y).toInt())

    fun floor(): Point = Point(Math.floor(x).toInt(), Math.floor(y).toInt())

    fun setAngle(angle: Angle, magnitude: Double = magnitude()): Vector {
        x = -magnitude * angle.sin()
        y = magnitude * angle.cos()
        return this
    }

    fun set(newVec: Vector) {
        x = newVec.x
        y = newVec.y
    }

    fun set(newX: Double, newY: Double) {
        x = newX
        y = newY
    }

    fun add(amountX: Double = 0.0, amountY: Double = 0.0): Vector {
        x += amountX
        y += amountY
        return this
    }

    operator fun plus(vector: Vector): Vector = Vector(x + vector.x, y + vector.y)

    operator fun minus(vector: Vector): Vector = Vector(x - vector.x, y - vector.y)

    operator fun times(scalar: Double): Vector = Vector(x * scalar, y * scalar)

    operator fun div(denominator: Double): Vector = Vector(x / denominator, y / denominator)

    operator fun plusAssign(vector: Vector) { add(vector.x, vector.y) }

    operator fun minusAssign(vector: Vector) { add(-vector.x, -vector.y) }

    operator fun timesAssign(scalar: Double) {
        x *= scalar
        y *= scalar
    }

    operator fun divAssign(denominator: Double) {
        x /= denominator
        y /= denominator
    }

    fun isZero(): Boolean = x == 0.0 && y == 0.0

    fun magnitude(): Double = Math.sqrt(x * x + y * y)

    fun normalise(): Vector {
        var mag = magnitude()
        if(mag != 0.0) { this /= mag }
        return this
    }

    fun angle(): Angle = Angle(Math.atan2(-x, y))

    fun rotateTo(newAngle: Angle) = setAngle(newAngle, magnitude())

    infix fun deltaAngle(other: Vector): Angle = (other - this).angle()

    infix fun dot(other: Vector): Double = x * other.x + y * other.y

    infix fun xDist(other: Vector): Double = x - other.x

    infix fun yDist(other: Vector): Double = y - other.y

    infix fun distSq(other: Vector): Double {
        var deltaX = this xDist other
        var deltaY = this yDist other
        return deltaX * deltaX + deltaY * deltaY
    }

    infix fun dist(other: Vector): Double = Math.sqrt(this distSq other)

    fun xf(): Float = x.toFloat()

    fun yf(): Float = y.toFloat()
}