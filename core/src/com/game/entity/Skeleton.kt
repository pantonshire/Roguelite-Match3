package com.game.entity

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.*
import com.game.state.RoomState

class Skeleton(room: RoomState, pos: Tile, id: Int): Enemy(room, pos, "skeleton", id) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("skeleton"))
    override val bounceHeight: Double = 5.0
    override val maxMoves: Int = 3

    var attackDirection: Direction? = null
    var bone: Bone? = null

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

        } else if(bone != null) {
            if(bone!!.isFinished()) {
                bone = null
            }

            else {
                bone!!.endIdle()
                bone!!.act()
                return true
            }
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
        return (path.isEmpty() || movesLeft == 0) && bone == null && attacksLeft == 0
    }


    override fun actionDelay(): Int {
        if(bone != null) {
            return bone!!.actionDelay()
        }

        return super.actionDelay()
    }


    override fun idle() {
        super.idle()
        if(bone != null) {
            bone!!.idle()
        }
    }


    override fun draw(canvas: GameCanvas) {
        if(bone != null) {
            bone!!.draw(canvas)
        }

        sprite.setRegion(0, 0, 24, 24)
        super.draw(canvas)
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
            bone = Bone(room, pos.copy(), attackDirection ?: Direction.NORTH)
            attackDirection = null
        }
    }

}