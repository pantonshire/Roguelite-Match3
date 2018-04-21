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

    abstract val sprite: TextureRegion


    fun move(direction: Direction): Boolean {
        val newPos = pos.offset(direction)
        if(!room.isEmpty(newPos)) {
            return false
        }

        pos.add(direction)
        room.checkForMatch()
        return true
    }

    fun forceMove(direction: Direction) {
        pos.add(direction)
        room.checkForMatch()
    }

    open fun drawPos(): Vector = tiles.getPositionOf(pos)

    open fun currentSpeed(): Double = speed

    open fun actionDelay(): Int = 15

    open fun draw(canvas: GameCanvas) {
        val drawPos = drawPos()
        canvas.draw(sprite, drawPos.xf(), drawPos.yf())
    }

    abstract fun act(): Boolean

    abstract fun startTurn()

    abstract fun endTurn()

    abstract fun onDied()

    abstract fun isFinished(): Boolean
}