package com.game.entity

import com.game.graphics.GameCanvas
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.state.RoomState
import java.awt.Point

abstract class Entity(val room: RoomState, val pos: Tile, private val speed: Double) {

    val tiles = room.tiles

    fun move(direction: Direction): Boolean {
        val newPos = pos.offset(direction)
        if(!room.isEmpty(newPos)) {
            println("NOT EMPTY")
            return false
        }

        println("MOVED")
        pos.add(direction)
        return true
    }

    fun forceMove(direction: Direction) {
        pos.add(direction)
    }

    open fun drawPos(): Vector = tiles.getPositionOf(pos)

    open fun currentSpeed(): Double = speed

    open fun actionDelay(): Int = 15

    abstract fun act(): Boolean

    abstract fun startTurn()

    abstract fun endTurn()

    abstract fun isFinished(): Boolean

    abstract fun draw(canvas: GameCanvas)
}