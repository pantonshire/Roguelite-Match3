package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.particle.AnimatedParticle
import com.game.run.Run
import com.game.state.RoomState

class Player(room: RoomState, pos: Tile): Entity(room, pos, 10.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("hero"))
    override val bounceHeight: Double = 5.0

    var movesLeft: Int = 0
    var attacksLeft: Int = 0
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

        if(attacksLeft > 0) {
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
                    val angle = attackDirection.angle()
                    room.particles.add(AnimatedParticle(drawPos() + Vector(0.0, 3.0) + Vector().setAngle(angle, 12.0), Vector(), "slash", Sequences.slash).setAngle(angle))
                    target.move(attackDirection)
                    target.knockback()
                    --attacksLeft
                    return true
                }
            }
        }

        return false
    }

    override fun startTurn() {
        movesLeft = Run.current.movements
        attacksLeft = Run.current.attacks
        endedEarly = false
    }

    override fun endTurn() {

    }

    override fun onDied() {

    }

    override fun isFinished(): Boolean {
        return (movesLeft == 0 && attacksLeft == 0) || endedEarly
    }
}