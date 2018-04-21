package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.*
import com.game.run.Run
import com.game.state.RoomState
import tilemap.PathFinder

class Enemy(room: RoomState, pos: Tile, val group: String): Entity(room, pos, 9.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("skeleton"))

    val pathFinder = PathFinder(room)
    var path: MutableList<Direction> = mutableListOf()

    var stunned = false
    var movesLeft: Int = 0
    var attackDirection: Direction? = null


    override fun act(): Boolean {
        if(path.isNotEmpty()) {
            move(path.first())
            path.removeAt(0)
            --movesLeft
            return true
        }

        return false
    }

    override fun startTurn() {
        movesLeft = 3
        path = pathFinder.getDirectionSequence(pos, room.player.pos, group)
    }

    override fun endTurn() {
        path.clear()
        stunned = false
    }

    override fun onDied() {

    }

    override fun isFinished(): Boolean {
        return path.isEmpty() || movesLeft == 0
    }

    override fun draw(canvas: GameCanvas) {
        super.draw(canvas)
        if(attackDirection != null) {

            val arrow = Textures.get("arrow")
            val angle = Angle(when(attackDirection) {
                Direction.NORTH -> 0.0
                Direction.WEST -> Const.TAU * 0.25
                Direction.SOUTH -> Const.TAU * 0.5
                else -> Const.TAU * 0.75
            })

            val drawPos = drawPos() + Vector(0.0, 3.0) + Vector().setAngle(angle, 12.0)

            canvas.draw(arrow, drawPos.xf(), drawPos.yf(), rotation = angle)
        }
    }

    fun chooseIntentions() {
        val player = room.player

        attackDirection = when {
            stunned -> null
            player.pos.x == pos.x && player.pos.y < pos.y -> Direction.SOUTH
            player.pos.x == pos.x && player.pos.y > pos.y -> Direction.NORTH
            player.pos.x < pos.x && player.pos.y == pos.y -> Direction.WEST
            player.pos.x > pos.x && player.pos.y == pos.y -> Direction.EAST
            else -> null
        }
    }
}