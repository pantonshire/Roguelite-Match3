package com.game.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.Animation
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.*
import com.game.particle.AnimatedParticle
import com.game.state.RoomState

class Slime(room: RoomState, pos: Tile, id: Int): Enemy(room, pos, 1.0, "slime", id) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("slime"))
    override val bounceHeight: Double= 10.0
    override val maxMoves: Int = 4


    override fun act(): Boolean {
        if(directions.isNotEmpty()) {
            val collidedEntity = room.entityAt(pos.offset(directions.first()))
            if(collidedEntity is Player) {
                collidedEntity.move(directions.first())
                collidedEntity.knockback()
                room.damagePlayer()
                directions.clear()
                path.clear()
                movesLeft = 0
                attack = true
            } else if(!move(directions.first())) {
                directions.clear()
                path.clear()
                movesLeft = 0
                attack = true
            } else {
                directions.removeAt(0)
                path.removeAt(0)
                --movesLeft
                if(movesLeft == 0) {
                    attack = true
                }
            }
            return true

        }

        return true
    }


    override fun chooseIntentions() {
        if(stunned) {
            return
        }

        val player = room.player

        path = pathFinder.findPath(pos, player.pos, group)
        if(!path.isEmpty()) {
            path.add(player.pos.copy())
        }

        if(path.size > maxMoves) {
            path = path.subList(0, maxMoves)
        }

        directions = pathFinder.toDirectionSequence(path)


        if(path.size < 2) {
            futurePos.set(pos.x, pos.y)
        } else {
            futurePos.set(path[path.size - 2].x, path[path.size - 2].y)
        }

    }


    override fun isFinished(): Boolean {
        return (path.isEmpty() || movesLeft == 0)
    }


    override fun drawFG(canvas: GameCanvas) {
        for(direction in Direction.values()) {
            val arrow = Textures.get("arrow")
            val angle = direction.angle()
            val drawPos = drawPos() + Vector(0.0, -5.0) + Vector().setAngle(angle, 14.0)
            canvas.draw(arrow, drawPos.xf(), drawPos.yf(), rotation = angle)
        }

//        canvas.draw(Textures.get("wisp"), (futurePos.x * tiles.tileSize).toFloat() + tiles.tileSize.toFloat() / 2f, futurePos.y * (tiles.tileSize).toFloat() + tiles.tileSize.toFloat() / 2f)
    }

}