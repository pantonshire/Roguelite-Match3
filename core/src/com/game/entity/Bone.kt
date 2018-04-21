package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.run.Run
import com.game.state.RoomState

class Bone(room: RoomState, pos: Tile, val direction: Direction): Entity(room, pos, 0.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("bone"))

    var movesLeft: Int = 10
    var collided: Boolean = false
    var horizontal: Boolean = false
    var waitingTicks: Int = 0

    override fun act(): Boolean {
        waitingTicks = 0

        forceMove(direction)
        if(!room.isEmpty(pos)) {
            val hitEntity = room.entityAt(pos)
            if(hitEntity != null) {
                if(hitEntity is Player) {
                    Run.current.loseHeart()
                } else if(hitEntity is Enemy) {
                    hitEntity.stun()
                }
            }

            collided = true
        }

        --movesLeft
        horizontal = !horizontal

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

    override fun draw(canvas: GameCanvas) {
        sprite.setRegion(if(horizontal) 15 else 0, 0, 15, 15)
        super.draw(canvas)
        ++waitingTicks
    }

    override fun drawPos(): Vector {
        if(collided) {
            return super.drawPos()
        }

        val offset: Vector = Vector(direction.x.toDouble() * waitingTicks, direction.y.toDouble() * waitingTicks)
        offset *= tiles.tileSize.toDouble() / 5.0
        return super.drawPos() + offset
    }
}