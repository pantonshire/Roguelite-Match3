package com.game.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.state.RoomState
import java.awt.Point

abstract class Entity(val room: RoomState, val pos: Tile, private val speed: Double) {

    val tiles = room.tiles
    val lastPos = pos.copy()
    var lastDirection: Direction = Direction.NORTH

    abstract val sprite: TextureRegion
    abstract val bounceHeight: Double

    var dead: Boolean = false
    var idleTicks: Int = 0
    var knockbackTicks: Int = 0


    fun move(direction: Direction): Boolean {
        lastPos.set(pos.x, pos.y)
        lastDirection = direction
        val newPos = pos.offset(direction)
        if(!room.isEmpty(newPos)) {
            return false
        }

        pos.add(direction)
        room.checkForMatch()
        onMoved(lastPos)
        return true
    }

    fun forceMove(direction: Direction) {
        lastPos.set(pos.x, pos.y)
        lastDirection = direction
        pos.add(direction)
        room.checkForMatch()
        onMoved(lastPos)
    }

    fun knockback() {
        knockbackTicks = actionDelay()
    }

    open fun idle() {
        ++idleTicks
    }

    open fun endIdle() {
        idleTicks = 0
        lastPos.set(pos.x, pos.y)
    }

    open fun drawPos(): Vector {
        return if(pos.x != lastPos.x || pos.y != lastPos.y) {
            val dist = if(knockbackTicks > 0) actionDelay() - knockbackTicks else idleTicks
            val bounce = if(knockbackTicks > 0) 2.0 else bounceHeight

            tiles.getPositionOf(lastPos) +
                    (Vector(lastDirection.x.toDouble(), lastDirection.y.toDouble())
                            * (dist * tiles.tileSize.toDouble() / actionDelay().toDouble())) +
                    Vector(0.0, Math.sin(Math.PI * dist.toDouble() / actionDelay().toDouble()) * bounce)
        } else {
            tiles.getPositionOf(pos)
        }
    }

    open fun currentSpeed(): Double = speed

    open fun actionDelay(): Int = 10

    open fun draw(canvas: GameCanvas) {
        val drawPos = drawPos()
        canvas.draw(sprite, drawPos.xf(), drawPos.yf())

        if(knockbackTicks > 0) {
            --knockbackTicks
            if(knockbackTicks == 0) {
                lastPos.set(pos.x, pos.y)
            }
        }
    }

    open fun drawBG(canvas: GameCanvas) {}
    open fun drawFG(canvas: GameCanvas) {}

    open fun onMoved(lastPos: Tile) {}

    abstract fun act(): Boolean

    abstract fun startTurn()

    abstract fun endTurn()

    abstract fun onDied()

    abstract fun isFinished(): Boolean
}