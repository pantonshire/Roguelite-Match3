package com.game.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.audio.SFX
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.*
import com.game.state.RoomState

class BlueWisp(room: RoomState, pos: Tile, id: Int): Enemy(room, pos, 4.25, "wisp", id) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("blue_wisp"))
    override val bounceHeight: Double = 7.0
    override val maxMoves: Int = 1

    var attackDirectionA: Direction? = null
    var attackDirectionB: Direction? = null
    var fireball: BlueFireball? = null

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

        attackDirectionA = RandomUtils.randDirection()
        attackDirectionB = RandomUtils.randDirection()
        attacksLeft = 2

    }


    override fun endTurn() {
        super.endTurn()
        attackDirectionA = null
        attackDirectionB = null
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

        super.draw(canvas)
    }


    override fun drawFG(canvas: GameCanvas) {
        if(attackDirectionA != null) {
            val arrow = Textures.get("arrow")
            val angle = attackDirectionA!!.angle()
            val drawPos = drawPos() + Vector().setAngle(angle, 12.0)
            canvas.draw(arrow, drawPos.xf(), drawPos.yf(), rotation = angle)
        }

        if(attackDirectionB != null) {
            val arrow = Textures.get("arrow")
            val angle = attackDirectionB!!.angle()
            val drawPos = drawPos() + Vector().setAngle(angle, 12.0)
            canvas.draw(arrow, drawPos.xf(), drawPos.yf(), rotation = angle)
        }
    }


    private fun attack() {
        if(attacksLeft == 1) {
            if(attackDirectionA != null) {
                fireball = BlueFireball(room, pos.copy(), attackDirectionA ?: Direction.NORTH)
                SFX.play("shoot")
            }
        } else {
            if(attackDirectionB != null) {
                fireball = BlueFireball(room, pos.copy(), attackDirectionB ?: Direction.NORTH)
                SFX.play("shoot")
            }
        }
    }

}