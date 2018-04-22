package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.particle.AnimatedParticle
import com.game.run.Run
import com.game.state.RoomState

class Fireball(room: RoomState, pos: Tile, val direction: Direction): Entity(room, pos, 0.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("fireball"))
    override val bounceHeight: Double = 0.0

    var movesLeft: Int = 3
    var collided: Boolean = false

    override fun act(): Boolean {
        room.particles.add(AnimatedParticle(drawPos(), Vector(), "small_explosion", Sequences.smallExplosion))

        forceMove(direction)
        if(!room.isEmpty(pos)) {
            val hitEntity = room.entityAt(pos)
            if(hitEntity != null) {
                if(hitEntity is Player) {
                    room.damagePlayer()
                } else if(hitEntity is Enemy) {
                    if(hitEntity.group != "wisp") {
                        hitEntity.stun()
                    }
                }
            }

            collided = true
        }

        --movesLeft

        return true
    }

    override fun startTurn() {

    }

    override fun endTurn() {

    }

    override fun onDied() {

    }

    override fun isFinished(): Boolean {
        return collided || movesLeft == 0
    }

    override fun actionDelay(): Int {
        return 3
    }
}