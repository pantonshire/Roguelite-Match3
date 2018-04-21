package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.run.Run
import com.game.state.RoomState
import tilemap.PathFinder

class Enemy(room: RoomState, pos: Tile): Entity(room, pos, 10.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("skeleton"))

    val pathFinder = PathFinder(room)
    var path: MutableList<Direction> = mutableListOf()

    var movesLeft: Int = 0


    override fun act(): Boolean {
        if(path.isNotEmpty()) {
            move(path.first())
            path.removeAt(0)
            return true
        }

        return false
    }

    override fun startTurn() {
        movesLeft = Run.current.movements
        path = pathFinder.getDirectionSequence(pos, room.player.pos)
    }

    override fun endTurn() {
        path.clear()
    }

    override fun onDied() {

    }

    override fun isFinished(): Boolean {
        return path.isEmpty()
    }
}