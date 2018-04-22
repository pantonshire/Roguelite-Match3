package com.game.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.game.entity.*
import com.game.graphics.GameCanvas
import com.game.graphics.Sequences
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.RandomUtils
import com.game.maths.Tile
import com.game.maths.Vector
import com.game.particle.AnimatedParticle
import com.game.particle.Particle
import com.game.particle.TextParticle
import com.game.run.Run
import tilemap.TileMap

class RoomState(playerPos: Tile, val north: Boolean, val east: Boolean, val south: Boolean, val west: Boolean, val tiles: TileMap = TileMap(32, 20, 24, "tiles", 5)): State() {

    val particles: MutableList<Particle> = mutableListOf()
    val entities: MutableList<Entity> = mutableListOf()
    val player: Player = Player(this, playerPos)

    var turnQueue: MutableList<Entity> = mutableListOf()
    val killSet: MutableSet<Entity> = mutableSetOf()
    var round = 0
    var delay = 0
    var lastEntity: Entity? = null
    var gameOver: Boolean = false
    var gameOverTicks: Int = 0
    var doorsLocked = false
    var enemyPathToShow: Int = -1


    init {
        entities.add(player)

//        for(i in 0 until tiles.width) {
//            for(j in 0..1) {
//                tiles.tiles[i][j] = 5
//                tiles.tiles[i][tiles.height - 1 - j] = 5
//            }
//        }
//
//        for(i in 0 until tiles.height) {
//            for(j in 0..7) {
//                tiles.tiles[j][i] = 5
//                tiles.tiles[tiles.width - 1 - j][i] = 5
//            }
//        }
//        entities.add(BlueWisp(this, Tile(10, 2), 0))
//        entities.add(BlueWisp(this, Tile(11, 3), 1))
//        entities.add(BlueWisp(this, Tile(12, 2), 2))
//        entities.add(BlueWisp(this, Tile(13, 2), 3))
    }


    override fun update() {
        particles.asSequence().forEach {
            it.update()
        }

        particles.removeIf { it.shouldRemove() }

        if(gameOver) {
            ++gameOverTicks
            if(gameOverTicks > 30 && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                //TODO: Return to main menu
            }
            return
        }

        if(delay <= 0) {
            while (killSet.isNotEmpty()) {
                killSet.asSequence().forEach {
                    killEntity(it)
                }
                killSet.clear()
                checkGroups()
            }

            if(combat()) {
                if(turnQueue.isEmpty()) {
                    lastEntity?.endIdle()
                    newRound()
                }

                if(isPlayerTurn() && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                    var nextId = -1
                    for(it in entities) {
                        if(it is Enemy) {
                            if(it.id > enemyPathToShow && (it.id < nextId || nextId == -1)) {
                                nextId = it.id
                            }
                        }
                    }

                    enemyPathToShow = nextId
                }

                val currentEntity = turnQueue.first()

                if(currentEntity.invincible) {
                    currentEntity.invincible = false
                }

                if(lastEntity != currentEntity) {
                    lastEntity?.endIdle()
                    currentEntity.startTurn()
                    lastEntity = currentEntity
                } else if(currentEntity.isFinished() || currentEntity.dead) {
                    turnQueue.removeAt(0)
                    currentEntity.endTurn()
                    delay = maxOf(delay, currentEntity.actionDelay())
                }

                entities.asSequence().forEach {
                    it.endIdle()
                }

                if(currentEntity.act()) {
                    delay = maxOf(delay, currentEntity.actionDelay())
                }
            } else {
                if(doorsLocked) {
                    doorsLocked = false
                    openDoors()
                }

                player.endIdle()
                if(player.act()) {
                    delay = maxOf(delay, player.actionDelay())
                }
            }
        } else {
            --delay
            if(combat()) {
                if(turnQueue.isNotEmpty()) {
                    turnQueue.first().idle()
                }
            } else {
                player.idle()
            }
        }
    }


    override fun drawGame(canvas: GameCanvas) {
        tiles.draw(canvas)

        entities.asSequence().filter { it !is Enemy || enemyPathToShow == -1 || it.id == enemyPathToShow }.forEach { it.drawBG(canvas) }
        entities.asSequence().forEach { it.draw(canvas) }

        particles.asSequence().forEach {
            it.draw(canvas)
        }

        if(isPlayerTurn()) {
            entities.asSequence().filter { it !is Enemy || enemyPathToShow == -1 || it.id == enemyPathToShow }.forEach { it.drawFG(canvas) }

            for(i in 1 until turnQueue.size) {
                canvas.drawText(i.toString(),
                        turnQueue[i].pos.x.toFloat() * tiles.tileSize + 2,
                        turnQueue[i].pos.y.toFloat() * tiles.tileSize + tiles.tileSize + 6,
                        "prstart", 8, Color.WHITE)
            }
        }
    }


    override fun drawHUD(canvas: GameCanvas) {
        for(i in 0 until Run.current.maxHealth) {
            canvas.draw(Textures.get(if(i >= Run.current.health) "empty_heart" else "heart"),340f, 580f - 20f * i)
        }

        for(i in 0 until Run.current.movements) {
            canvas.draw(Textures.get(if(i >= player.movesLeft) "empty_boot" else "boot"), 360f, 580f - 20f * i)
        }

        for(i in 0 until Run.current.attacks) {
            canvas.draw(Textures.get(if(i >= player.attacksLeft) "empty_sword" else "sword"), 380f, 580f - 20f * i)
        }

        if(combat()) {
            if(isPlayerTurn()) {
                canvas.drawText("Q: End turn", 840f, 580f, "prstart", 8, Color.WHITE)
                canvas.drawText("WASD: Move", 840f, 560f, "prstart", 8, Color.WHITE)
                canvas.drawText("Arrow keys:", 838f, 540f, "prstart", 8, Color.WHITE)
                canvas.drawText("Attack", 838f, 528f, "prstart", 8, Color.WHITE)
                canvas.drawText("Tab: view", 838f, 508f, "prstart", 8, Color.WHITE)
                canvas.drawText("a single", 838f, 496f, "prstart", 8, Color.WHITE)
                canvas.drawText("enemy\'s path", 838f, 484f, "prstart", 8, Color.WHITE)
            } else {
                canvas.drawText("Enemy\'s turn", 838f, 580f, "prstart", 8, Color.WHITE)
            }
        } else {
            canvas.drawText("Victory!", 838f, 580f, "prstart", 8, Color.WHITE)
            canvas.drawText("Time to go to", 838f, 560f, "prstart", 8, Color.WHITE)
            canvas.drawText("the next room", 838f, 540f, "prstart", 8, Color.WHITE)
        }

        if(gameOverTicks > 30) {
            canvas.drawText("GAME OVER (PRESS SPACE)", 460f, 420f, "prstart", 16, Color.WHITE)
        }
    }


    private fun newRound() {
        ++round
        delay = 40
        enemyPathToShow = -1
        turnQueue = entities.sortedWith(compareBy({ -it.currentSpeed() })).toMutableList()
        lastEntity = null
        chooseEnemyIntentions()
    }

    private fun killEntity(entity: Entity) {
        if(!entity.dead && !entity.invincible) {
            entities.remove(entity)
            if (entity in turnQueue) {
                turnQueue.remove(entity)
            }

            particles.add(0, AnimatedParticle(entity.drawPos(), Vector(), "explosion", Sequences.explosion))
            entity.endIdle()
            entity.dead = true
            entity.onDied()
        }
    }


    private fun checkGroups() {
        entities.asSequence().forEach {
            if(it is Enemy) {
                val enemy: Enemy = it
                val group = it.group
                var others = 0
                entities.asSequence().forEach {
                    if(it != enemy && it is Enemy) {
                        if(it.group == group) {
                            ++others
                        }
                    }
                }

                if(others < 2) {
                    entities.filter { it is Enemy && it.group == group }.forEach { killSet += it }
                }
            }
        }
    }


    private fun chooseEnemyIntentions() {
        turnQueue.asSequence().forEach {
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
        var foundMatch = false
        var textPos = Vector(0.0, 0.0)

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
                        if(!foundMatch) {
                            foundMatch = true
                            textPos = chain.first().drawPos()
                        }
//                        chain.asSequence().forEach { it.endIdle() }
//                        delay = 20
                    }
                }
            }
        }

        if(foundMatch) {
            particles.add(TextParticle(textPos, Vector(y = 0.25), 60, RandomUtils.randEncouragement(), "prstart", 8, Color.WHITE).setTimer(10))
        }
    }


    fun damagePlayer() {
        Run.current.loseHeart()
        particles.add(AnimatedParticle(player.drawPos(), Vector(), "hurt", Sequences.smallExplosion))
        if(Run.current.health <= 0) {
            gameOver = true
            entities.remove(player)
        }
    }


    fun combat(): Boolean = entities.size > 1


    fun openDoors() {
        if(north) {
            tiles.tiles[15][16] = 1
            tiles.tiles[16][16] = 1
        }
        if(south) {
            tiles.tiles[15][3] = 1
            tiles.tiles[16][3] = 1
        }
        if(east) {
            tiles.tiles[22][9] = 1
            tiles.tiles[22][10] = 1
        }
        if(west) {
            tiles.tiles[9][9] = 1
            tiles.tiles[9][10] = 1
        }
    }


    fun closeDoors() {
        if(north) {
            tiles.tiles[15][16] = 6
            tiles.tiles[16][16] = 6
        }
        if(south) {
            tiles.tiles[15][3] = 6
            tiles.tiles[16][3] = 6
        }
        if(east) {
            tiles.tiles[22][9] = 7
            tiles.tiles[22][10] = 7
        }
        if(west) {
            tiles.tiles[9][9] = 7
            tiles.tiles[9][10] = 7
        }
    }

}