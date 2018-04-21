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

    var turnQueue: MutableList<Entity> = mutableListOf()
    val killSet: MutableSet<Entity> = mutableSetOf()
    var round = 0
    var delay = 0
    var lastEntity: Entity? = null

    init {
        entities.add(Player(this, Tile(0, 0)))
        entities.add(Enemy(this, Tile(10, 2)))
        entities.add(Enemy(this, Tile(11, 3)))
        entities.add(Enemy(this, Tile(12, 2)))
        entities.add(Enemy(this, Tile(13, 2)))
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

                if(currentEntity.isFinished()) {
                    turnQueue.removeAt(0)
                    currentEntity.endTurn()

                } else if(currentEntity.act()) {
                    delay = maxOf(delay, currentEntity.actionDelay())
                }

            }
        } else {
            --delay
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
        turnQueue = entities.sortedWith(compareBy({ it.currentSpeed() })).toMutableList()
        lastEntity = null
        println("ROUND $round")
    }

    private fun killEntity(entity: Entity) {
        entities.remove(entity)
        if(entity in turnQueue) {
            turnQueue.remove(entity)
        }

        entity.onDied()
    }

    fun isEmpty(tile: Tile, vararg ignore: Entity): Boolean {
        if(tiles.isSolid(tile)) {
            return false
        }

        entities.asSequence().forEach {
            if(it !in ignore && it.pos.x == tile.x && it.pos.y == tile.y) {
                return false
            }
        }

        return true
    }

    fun entityAt(tile: Tile, vararg ignore: Entity): Entity? {
        entities.asSequence().forEach {
            if(it !in ignore && it.pos.x == tile.x && it.pos.y == tile.y) {
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
                            chain += nextEntity
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