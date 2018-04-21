package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.*
import com.game.particle.AnimatedParticle
import com.game.particle.TextParticle
import com.game.run.Run
import com.game.state.RoomState
import tilemap.PathFinder

class Enemy(room: RoomState, pos: Tile, val group: String, val id: Int): Entity(room, pos, 9.0) {

    override val sprite: TextureRegion = TextureRegion(Textures.get("skeleton"))
    override val bounceHeight: Double = 5.0

    val maxMoves = 3

    val pathFinder = PathFinder(room)
    var path: MutableList<Tile> = mutableListOf()
    var directions: MutableList<Direction> = mutableListOf()
    val futurePos: Tile = pos.copy()
    var pathLocked = false
    var walking = false

    var stunned = false
    var movesLeft: Int = 0
    var attackDirection: Direction? = null
    var bone: Bone? = null
    var attack = false
    var attacks = 0


    override fun act(): Boolean {
        if(directions.isNotEmpty()) {
            if(!move(directions.first())) {
                directions.clear()
                path.clear()
                movesLeft = 0
                attack = true
                walking = false
            } else {
                walking = true
                directions.removeAt(0)
                path.removeAt(0)
                --movesLeft
                if(movesLeft == 0) {
                    attack = true
                    walking = false
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
                ++attacks

            } else if(attacks == 0) {
                attack = true
            }

            return true
        }

        return true
    }


    override fun startTurn() {
        movesLeft = maxMoves
        pathLocked = true
        attack = false
        attacks = 0
    }


    override fun endTurn() {
        path.clear()
        directions.clear()
        stunned = false
        pathLocked = false
    }


    override fun onDied() {

    }


    override fun isFinished(): Boolean {
        return (path.isEmpty() || movesLeft == 0) && bone == null && !attack
    }


    override fun actionDelay(): Int {
        if(bone != null) {
            return 5
        }

        return super.actionDelay()
    }


    override fun idle() {
        super.idle()
        if(bone != null) {
            bone!!.idle()
        }
    }


    override fun onMoved(lastPos: Tile) {
        super.onMoved(lastPos)
        if(!pathLocked) {
            val delta = lastPos.delta(pos)
            path.asSequence().forEach {
                it.add(delta.x, delta.y)
            }
        }
    }


    override fun draw(canvas: GameCanvas) {
        if(bone != null) {
            bone!!.draw(canvas)
        }


        sprite.setRegion(0, 0, 24, 24)
        super.draw(canvas)
    }


    override fun drawBG(canvas: GameCanvas) {
        if(!path.isEmpty()) {
            val pathTexture = Textures.get("enemy_path")
            val pathColour = getPathColour()
            canvas.tint(Color(pathColour.r, pathColour.g, pathColour.b, 0.25f))
            canvas.draw(pathTexture, (pos.x * tiles.tileSize + tiles.tileSize / 2).toFloat(), (pos.y * tiles.tileSize + tiles.tileSize / 2).toFloat())
            path.asSequence().forEach {
                canvas.draw(pathTexture, (it.x * tiles.tileSize + tiles.tileSize / 2).toFloat(), (it.y * tiles.tileSize + tiles.tileSize / 2).toFloat())
            }
            canvas.removeTint()
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


    fun chooseIntentions() {
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
    }


    fun stun() {
        room.particles.add(TextParticle(drawPos(), Vector(y = 0.5), 60, "STUNNED", "orangekid", 12, Color.WHITE))
        stunned = true
        attackDirection = null
        path.clear()
        directions.clear()
    }


    private fun attack() {
        if(attackDirection != null) {
            bone = Bone(room, pos.copy(), attackDirection ?: Direction.NORTH)
            attackDirection = null
        }
    }


    private fun getPathColour(): Color {
        return when(id % 10) {
            0 -> Color.WHITE
            1 -> Color.BLUE
            2 -> Color.RED
            3 -> Color.GREEN
            4 -> Color.YELLOW
            5 -> Color.PURPLE
            6 -> Color.CORAL
            7 -> Color.FIREBRICK
            8 -> Color.LIME
            9 -> Color.PINK
            else -> Color.WHITE
        }
    }

}