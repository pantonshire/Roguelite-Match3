package com.game.state

import com.badlogic.gdx.graphics.Color
import com.game.entity.Entity
import com.game.entity.Player
import com.game.entity.Enemy
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.particle.AnimatedParticle
import com.game.particle.Particle
import com.game.run.Run
import tilemap.TileMap

class RoomState: State() {

    val tiles: TileMap = TileMap(50, 50, 24, "tiles", 5)
    val particles: MutableList<Particle> = mutableListOf()
    val entities: MutableList<Entity> = mutableListOf()
    val player: Player = Player(this, Tile(0, 0))

    var turnQueue: MutableList<Entity> = mutableListOf()
    val killSet: MutableSet<Entity> = mutableSetOf()
    var round = 0
    var delay = 0
    var lastEntity: Entity? = null


    init {
        entities.add(player)
        entities.add(Enemy(this, Tile(10, 2), "skeleton", 0))
        entities.add(Enemy(this, Tile(11, 3), "skeleton", 1))
        entities.add(Enemy(this, Tile(12, 2), "skeleton", 2))
        entities.add(Enemy(this, Tile(13, 2), "skeleton", 3))
    }


    override fun update() {
        particles.asSequence().forEach {
            it.update()
        }

        particles.removeIf { it.shouldRemove() }

        if(delay <= 0) {
            if(killSet.isNotEmpty()) {
                killSet.asSequence().forEach {
                    killEntity(it)
                }
            }

            if(entities.isNotEmpty()) {

                if(turnQueue.isEmpty()) {
                    lastEntity?.endIdle()
                    newRound()
                }

                val currentEntity = turnQueue.first()

                if(lastEntity != currentEntity) {
                    lastEntity?.endIdle()
                    currentEntity.startTurn()
                    lastEntity = currentEntity
                }

                if(currentEntity.isFinished() || currentEntity.dead) {
                    turnQueue.removeAt(0)
                    currentEntity.endTurn()
                    delay = maxOf(delay, currentEntity.actionDelay())

                } else {
                    currentEntity.endIdle()
                    if(currentEntity.act()) {
                        delay = maxOf(delay, currentEntity.actionDelay())
                    }
                }

            }
        } else {
            --delay
            if(turnQueue.isNotEmpty()) {
                turnQueue.first().idle()
            }
        }
    }


    override fun drawGame(canvas: GameCanvas) {
        tiles.draw(canvas)

        entities.asSequence().forEach { it.drawBG(canvas) }
        entities.asSequence().forEach { it.draw(canvas) }
        entities.asSequence().forEach { it.drawFG(canvas) }

        particles.asSequence().forEach {
            it.draw(canvas)
        }

        if(isPlayerTurn()) {
            for(i in 1 until turnQueue.size) {
                canvas.drawText(i.toString(),
                        turnQueue[i].pos.x.toFloat() * tiles.tileSize + 2,
                        turnQueue[i].pos.y.toFloat() * tiles.tileSize + tiles.tileSize + 6,
                        "orangekid", 12, Color.WHITE)
            }
        }
    }


    override fun drawHUD(canvas: GameCanvas) {
        for(i in 0 until Run.current.maxHealth) {
            canvas.draw(Textures.get(if(i >= Run.current.health) "empty_heart" else "heart"), 20f * i + 340f, 580f)
        }
    }


    private fun newRound() {
        ++round
        delay = 40
        turnQueue = entities.sortedWith(compareBy({ -it.currentSpeed() })).toMutableList()
        lastEntity = null
        chooseEnemyIntentions()
    }

    private fun killEntity(entity: Entity) {
        if(!entity.dead) {
            entities.remove(entity)
            if (entity in turnQueue) {
                turnQueue.remove(entity)
            }

            particles.add(AnimatedParticle(entity.drawPos(), Vector(), "explosion", Sequences.explosion))
            entity.endIdle()
            entity.dead = true
            entity.onDied()
        }
    }


    private fun chooseEnemyIntentions() {
        entities.asSequence().forEach {
            if(it is Enemy) {
                it.chooseIntentions()
            }
        }
    }


    private fun isPlayerTurn(): Boolean = !turnQueue.isEmpty() && turnQueue.first() is Player


    fun isEmpty(tile: Tile, futurePositions: Boolean = false, vararg ignore: Entity): Boolean {
        if(tiles.isSolid(tile)) {
            return false
        }

        entities.asSequence().forEach {
            var entityX = it.pos.x
            var entityY = it.pos.y
            if(futurePositions && it is Enemy) {
                entityX = it.futurePos.x
                entityY = it.futurePos.y
            }

            if(it !in ignore && entityX == tile.x && entityY == tile.y) {
                return false
            }
        }

        return true
    }


    fun entityAt(tile: Tile, futurePositions: Boolean = false, vararg ignore: Entity): Entity? {
        entities.asSequence().forEach {
            var entityX = it.pos.x
            var entityY = it.pos.y
            if(futurePositions && it is Enemy) {
                entityX = it.futurePos.x
                entityY = it.futurePos.y
            }

            if(it !in ignore && entityX == tile.x && entityY == tile.y) {
                return it
            }
        }

        return null
    }


    fun checkForMatch() {
        entities.asSequence().forEach {
            if(it is Enemy) {
                val rootEntity = it
                val rootPos = it.pos

                Direction.values().asSequence().forEach {
                    val chain: MutableSet<Enemy> = mutableSetOf(rootEntity)
                    for(distance in 1..10) {
                        val nextEntity = entityAt(rootPos.offset(it, distance))
                        if(nextEntity is Enemy) {
                            if(nextEntity.group == rootEntity.group) {
                                chain += nextEntity
                            } else {
                                break
                            }
                        } else {
                            break
                        }
                    }

                    if(chain.size >= 3) {
                        killSet.addAll(chain)
//                        chain.asSequence().forEach { it.endIdle() }
//                        delay = 20
                    }
                }
            }
        }
    }

}