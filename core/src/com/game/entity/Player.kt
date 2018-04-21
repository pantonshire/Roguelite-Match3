package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.run.Run
import com.game.state.RoomState

class Player(room: RoomState, pos: Tile): Entity(room, pos, 10.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("hero"))
    override val bounceHeight: Double = 5.0

    var movesLeft: Int = 0
    var attacked: Boolean = false
    var endedEarly: Boolean = false


    override fun act(): Boolean {
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            endedEarly = true
            return true
        }

        if(movesLeft > 0 && (
                (Gdx.input.isKeyJustPressed(Input.Keys.W) && move(Direction.NORTH)) ||
                (Gdx.input.isKeyJustPressed(Input.Keys.A) && move(Direction.WEST)) ||
                (Gdx.input.isKeyJustPressed(Input.Keys.S) && move(Direction.SOUTH)) ||
                (Gdx.input.isKeyJustPressed(Input.Keys.D) && move(Direction.EAST))
        )) {
            --movesLeft
            return true
        }

        if(!attacked) {
            val attackDirection: Direction? = when {
                Gdx.input.isKeyJustPressed(Input.Keys.UP) -> Direction.NORTH
                Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> Direction.SOUTH
                Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> Direction.WEST
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> Direction.EAST
                else -> null
            }

            if(attackDirection != null) {
                val target = room.entityAt(pos.offset(attackDirection))
                if(target != null) {
                    target.move(attackDirection)
                    attacked = true
                    return true
                }
            }
        }

        return false
    }

    override fun startTurn() {
        movesLeft = Run.current.movements
        attacked = false
        endedEarly = false
    }

    override fun endTurn() {

    }

    override fun onDied() {

    }

    override fun isFinished(): Boolean {
        return (movesLeft == 0 && attacked) || endedEarly
    }
}