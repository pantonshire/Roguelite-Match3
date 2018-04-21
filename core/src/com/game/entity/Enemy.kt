package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.*
import com.game.run.Run
import com.game.state.RoomState
import tilemap.PathFinder

class Enemy(room: RoomState, pos: Tile, val group: String): Entity(room, pos, 9.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("skeleton"))

    val maxMoves = 3

    val pathFinder = PathFinder(room)
    var path: MutableList<Tile> = mutableListOf()
    var directions: MutableList<Direction> = mutableListOf()
    val futurePos: Tile = pos.copy()

    var stunned = false
    var movesLeft: Int = 0
    var attackDirection: Direction? = null


    override fun act(): Boolean {
        if(directions.isNotEmpty()) {
            move(directions.first())
            directions.removeAt(0)
            path.removeAt(0)
            --movesLeft
            return true
        }

        return false
    }


    override fun startTurn() {
        movesLeft = maxMoves
    }


    override fun endTurn() {
        path.clear()
        directions.clear()
        stunned = false
    }


    override fun onDied() {

    }


    override fun isFinished(): Boolean {
        return path.isEmpty() || movesLeft == 0
    }


    override fun draw(canvas: GameCanvas) {
        if(!path.isEmpty()) {
            val pathTexture = Textures.get("enemy_path")
            canvas.tint(Color(1.0f, 1.0f, 1.0f, 0.25f))
            canvas.draw(pathTexture, (pos.x * tiles.tileSize + tiles.tileSize / 2).toFloat(), (pos.y * tiles.tileSize + tiles.tileSize / 2).toFloat())
            path.asSequence().forEach {
                canvas.draw(pathTexture, (it.x * tiles.tileSize + tiles.tileSize / 2).toFloat(), (it.y * tiles.tileSize + tiles.tileSize / 2).toFloat())
            }
            canvas.removeTint()
        }

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

        path = pathFinder.findPath(pos, player.pos, group)
        if(path.size > maxMoves) {
            path = path.subList(0, maxMoves)
        }

        directions = pathFinder.toDirectionSequence(path)


        if(path.isEmpty()) {
            futurePos.set(pos.x, pos.y)
        } else {
            futurePos.set(path.last().x, path.last().y)
        }

        attackDirection = when {
            stunned -> null
            player.pos.x == futurePos.x && player.pos.y < futurePos.y -> Direction.SOUTH
            player.pos.x == futurePos.x && player.pos.y > futurePos.y -> Direction.NORTH
            player.pos.x < futurePos.x && player.pos.y == futurePos.y -> Direction.WEST
            player.pos.x > futurePos.x && player.pos.y == futurePos.y -> Direction.EAST
            else -> null
        }
    }

}