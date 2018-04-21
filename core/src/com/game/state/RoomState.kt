package com.game.state

import com.game.entity.Entity
import com.game.entity.Player
import com.game.entity.Enemy
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.run.Run
import tilemap.TileMap

class RoomState: State() {

    val tiles: TileMap = TileMap(50, 50, 24, "tiles", 5)
    val entities: MutableList<Entity> = mutableListOf()
    val player: Player = Player(this, Tile(0, 0))

    var turnQueue: MutableList<Entity> = mutableListOf()
    val killSet: MutableSet<Entity> = mutableSetOf()
    var round = 0
    var delay = 0
    var lastEntity: Entity? = null


    init {
        entities.add(player)
        entities.add(Enemy(this, Tile(10, 2), "skeleton"))
        entities.add(Enemy(this, Tile(11, 3), "skeleton"))
        entities.add(Enemy(this, Tile(12, 2), "skeleton"))
        entities.add(Enemy(this, Tile(13, 2), "skeleton"))
    }


    override fun update() {
        if(delay <= 0) {
            if(killSet.isNotEmpty()) {
                killSet.asSequence().forEach {
                    killEntity(it)
                }
            }

            if(entities.isNotEmpty()) {

                if(turnQueue.isEmpty()) {
                    newRound()
                }

                val currentEntity = turnQueue.first()

                if(lastEntity != currentEntity) {
                    currentEntity.startTurn()
                    lastEntity = currentEntity
                }

                currentEntity.endIdle()
                if(currentEntity.act()) {
                    delay = maxOf(delay, currentEntity.actionDelay())
                }

                if(currentEntity.isFinished()) {
                    turnQueue.removeAt(0)
                    currentEntity.endTurn()

                }

            }
        } else {
            --delay
            if(turnQueue.isNotEmpty()) {
                turnQueue.first().idleTicks += 1
            }
        }
    }


    override fun drawGame(canvas: GameCanvas) {
        tiles.draw(canvas)
        entities.asSequence().forEach {
            it.draw(canvas)
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
        entities.remove(entity)
        if(entity in turnQueue) {
            turnQueue.remove(entity)
        }

        entity.dead = true
        entity.onDied()
    }


    private fun chooseEnemyIntentions() {
        entities.asSequence().forEach {
            if(it is Enemy) {
                it.chooseIntentions()
            }
        }
    }


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
                        delay = 20
                    }
                }
            }
        }
    }

}