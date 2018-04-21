package com.game.state

import com.game.entity.Entity
import com.game.entity.Player
import com.game.graphics.GameCanvas
import com.game.maths.Tile
import tilemap.TileMap

class RoomState: State() {

    val tiles: TileMap = TileMap(50, 50, 24, "tiles", 5)
    val entities: MutableList<Entity> = mutableListOf()

    var turnQueue: MutableList<Entity> = mutableListOf()
    var round = 0
    var delay = 0
    var lastEntity: Entity? = null

    init {
        entities.add(Player(this, Tile(0, 0)))
    }

    override fun update() {
        if(entities.isNotEmpty()) {
            if(turnQueue.isEmpty()) {
                newRound()
            }

            if(delay <= 0) {
                val currentEntity = turnQueue.first()

                if(lastEntity != currentEntity) {
                    currentEntity.startTurn()
                    lastEntity = currentEntity
                }

                if(currentEntity.isFinished()) {
                    turnQueue.removeAt(0)
                    currentEntity.endTurn()

                } else if(currentEntity.act()) {
                    delay = currentEntity.actionDelay()
                }

            } else {
                --delay
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

    }

    private fun newRound() {
        ++round
        turnQueue = entities.sortedWith(compareBy({ it.currentSpeed() })).toMutableList()
        lastEntity = null
        println("ROUND $round")
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
}