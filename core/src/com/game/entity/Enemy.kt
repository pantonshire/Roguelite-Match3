package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.*
import com.game.particle.AnimatedParticle
import com.game.particle.TextParticle
import com.game.run.Run
import com.game.state.RoomState
import tilemap.PathFinder

abstract class Enemy(room: RoomState, pos: Tile, val group: String, val id: Int): Entity(room, pos, 9.0) {

    abstract val maxMoves: Int

    val pathFinder = PathFinder(room)
    var path: MutableList<Tile> = mutableListOf()
    var directions: MutableList<Direction> = mutableListOf()
    val futurePos: Tile = pos.copy()
    var pathLocked = false

    var stunned = false
    var movesLeft: Int = 0
    var attack = false
    var attacksLeft = 0

    override fun startTurn() {
        movesLeft = maxMoves
        pathLocked = true
        attack = false
    }


    override fun endTurn() {
        path.clear()
        directions.clear()
        stunned = false
        pathLocked = false
        attack = false
        attacksLeft = 0
    }


    override fun onDied() {

    }


    override fun onMoved(lastPos: Tile) {
        super.onMoved(lastPos)
        if(!pathLocked) {
            val delta = lastPos.delta(pos)
            path.asSequence().forEach {
                it.add(delta.x, delta.y)
            }
        }
    }


    override fun drawBG(canvas: GameCanvas) {
        if(!path.isEmpty()) {
            val pathTexture = Textures.get("enemy_path")
            val pathColour = getPathColour()
            canvas.tint(Color(pathColour.r, pathColour.g, pathColour.b, 0.25f))
            canvas.draw(pathTexture, (pos.x * tiles.tileSize + tiles.tileSize / 2).toFloat(), (pos.y * tiles.tileSize + tiles.tileSize / 2).toFloat())
            path.asSequence().forEach {
                canvas.draw(pathTexture, (it.x * tiles.tileSize + tiles.tileSize / 2).toFloat(), (it.y * tiles.tileSize + tiles.tileSize / 2).toFloat())
            }
            canvas.removeTint()
        }
    }


    abstract fun chooseIntentions()


    fun stun() {
        room.particles.add(TextParticle(drawPos(), Vector(y = 0.5), 60, "STUNNED", "orangekid", 12, Color.WHITE))
        stunned = true
        attacksLeft = 0
        attack = false
        path.clear()
        directions.clear()
    }


    private fun getPathColour(): Color {
        return when(id % 10) {
            0 -> Color.WHITE
            1 -> Color.BLUE
            2 -> Color.RED
            3 -> Color.GREEN
            4 -> Color.YELLOW
            5 -> Color.PURPLE
            6 -> Color.CORAL
            7 -> Color.FIREBRICK
            8 -> Color.LIME
            9 -> Color.PINK
            else -> Color.WHITE
        }
    }

}