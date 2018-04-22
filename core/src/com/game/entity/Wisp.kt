package com.game.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.audio.SFX
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.*
import com.game.state.RoomState

class Wisp(room: RoomState, pos: Tile, id: Int): Enemy(room, pos, 4.5, "wisp", id) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("wisp"))
    override val bounceHeight: Double = 7.0
    override val maxMoves: Int = 1

    var fireball: Fireball? = null

    override fun act(): Boolean {
        if(directions.isNotEmpty()) {
            if(!move(directions.first())) {
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

        } else if(fireball != null) {
            if(fireball!!.isFinished()) {
                fireball = null
            }

            else {
                fireball!!.endIdle()
                fireball!!.act()
                return true
            }
        } else {
            if(attack) {
                --attacksLeft
                attack()
                attack = false

            } else if(attacksLeft > 0) {
                attack = true
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
        if(path.size > maxMoves) {
            path = path.subList(0, maxMoves)
        }

        directions = pathFinder.toDirectionSequence(path)


        if(path.isEmpty()) {
            futurePos.set(pos.x, pos.y)
        } else {
            futurePos.set(path.last().x, path.last().y)
        }

        attacksLeft = 4

    }


    override fun isFinished(): Boolean {
        return (path.isEmpty() || movesLeft == 0) && fireball == null && attacksLeft == 0
    }


    override fun actionDelay(): Int {
        if(fireball != null) {
            return fireball!!.actionDelay()
        }

        return super.actionDelay()
    }


    override fun idle() {
        super.idle()
        if(fireball != null) {
            fireball!!.idle()
        }
    }


    override fun draw(canvas: GameCanvas) {
        if(fireball != null) {
            fireball!!.draw(canvas)
        }

        sprite.setRegion(0, 0, 24, 24)
        super.draw(canvas)
    }


    override fun drawFG(canvas: GameCanvas) {
        for(i in 0 until minOf(Direction.values().size, attacksLeft)) {
            val direction = Direction.values()[i]
            val arrow = Textures.get("arrow")
            val angle = Angle(when(direction) {
                Direction.NORTH -> 0.0
                Direction.WEST -> Const.TAU * 0.25
                Direction.SOUTH -> Const.TAU * 0.5
                else -> Const.TAU * 0.75
            })

            val drawPos = drawPos() + Vector().setAngle(angle, 12.0)

            canvas.draw(arrow, drawPos.xf(), drawPos.yf(), rotation = angle)
        }
    }


    private fun attack() {
        fireball = Fireball(room, pos.copy(), Direction.values()[attacksLeft % 4])
        SFX.play("shoot")
    }

}