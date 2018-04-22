package com.game.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.Animation
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.*
import com.game.particle.AnimatedParticle
import com.game.state.RoomState

class Vampire(room: RoomState, pos: Tile, id: Int): Enemy(room, pos, "vampire", id) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("vampire"))
    override val bounceHeight: Double
        get() = if(willFly) 0.0 else 5.0
    override val maxMoves: Int
        get() = if(willFly) 15 else 2

    private val batAnimation: Animation = Animation(Textures.get("bat"), Sequences.bat)

    var attackDirection: Direction? = null
    var willFly = false
    var bat = false


    override fun startTurn() {
        super.startTurn()
        if(willFly) {
            bat = true
        }
    }


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

        } else {
            if(attack) {
                attack()
                attack = false
                --attacksLeft

            } else if(attacksLeft > 0) {
                attack = true
            }

            return true
        }
    }


    override fun chooseIntentions() {
        if(stunned) {
            return
        }

        willFly = RandomUtils.chance(0.3)

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
            else -> {
                if(RandomUtils.chance(0.5)) {
                    val horizontal = RandomUtils.flipCoin()
                    if(horizontal) {
                        if(player.pos.x < futurePos.x) {
                            Direction.WEST
                        } else {
                            Direction.EAST
                        }
                    } else {
                        if(player.pos.y < futurePos.y) {
                            Direction.SOUTH
                        } else {
                            Direction.NORTH
                        }
                    }
                } else {
                    null
                }
            }
        }

        if(attackDirection != null) {
            attacksLeft = 1
        }

    }


    override fun isFinished(): Boolean {
        return (path.isEmpty() || movesLeft == 0) && attacksLeft == 0
    }


    override fun endTurn() {
        super.endTurn()
        attackDirection = null
        bat = false
        willFly = false
    }


    override fun draw(canvas: GameCanvas) {
        if(bat) {
            batAnimation.updateAnimation()
            val drawPos = drawPos()
            batAnimation.draw(canvas, drawPos.xf(), drawPos.yf())
        } else {
            super.draw(canvas)
        }
    }


    override fun drawFG(canvas: GameCanvas) {
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


    private fun attack() {
        if(attackDirection != null) {
            val direction = attackDirection!!
            val angle = direction.angle()
            room.particles.add(AnimatedParticle(drawPos() + Vector(0.0, 3.0) + Vector().setAngle(angle, 12.0), Vector(), "slash", Sequences.slash).setAngle(angle))
            val target = room.entityAt(pos.offset(direction))
            if(target != null) {
                target.move(direction)
                target.knockback()
                if(target is Player) {
                    room.damagePlayer()
                } else if(target is Enemy) {
                    target.stun()
                }
            }
        }
    }

}