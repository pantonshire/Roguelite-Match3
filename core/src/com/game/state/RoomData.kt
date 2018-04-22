package com.game.state

import com.game.entity.*
import com.game.maths.Direction
import com.game.maths.RandomUtils
import com.game.maths.Tile
import tilemap.TileMap
import java.util.*

class RoomData(val difficulty: Int,
               var north: Boolean, var east: Boolean, var south: Boolean, var west: Boolean,
               val objects: Array<Pair<Char, Tile>>, val boss: Boolean = false) {

    val tiles: TileMap = TileMap(32, 20, 24, "tiles", 5)
    val enemyMap: Array<String> = newEnemyMap()
    var cleared: Boolean = false

    init {
        for(i in 0 until tiles.width) {
            for(j in 0..3) {
                tiles.tiles[i][j] = 5
                tiles.tiles[i][tiles.height - 1 - j] = 5
            }
        }

        for(i in 0 until tiles.height) {
            for(j in 0..9) {
                tiles.tiles[j][i] = 5
                tiles.tiles[tiles.width - 1 - j][i] = 5
            }
        }
    }


    fun makeRoom(enteredFrom: Direction): RoomState {

        val playerPos = when(enteredFrom.opposite()) {
            Direction.NORTH -> Tile(15, 15)
            Direction.SOUTH -> Tile(15, 4)
            Direction.EAST -> Tile(21, 10)
            else -> Tile(10, 10)
        }

        val room = RoomState(playerPos, north, east, south, west, tiles)

        if(boss) {
            room.ladderPos.set(20, 14)
        }

        var enemies = 0

        objects.asSequence().forEach {
            when {
                it.first == 'w' -> tiles.tiles[it.second.x + 10][it.second.y + 4] = 5

                it.first.isDigit() && !cleared -> {
                    val group: Int = intValueOf(it.first)
                    val newEnemy: Enemy = makeEnemy(room, it.second, group, enemies)
                    room.entities.add(newEnemy)
                    ++enemies
                }

                it.first == 'b' && !cleared -> {
                    makeBoss(room, it.second, enemies)
                    room.entities.add(makeBoss(room, it.second, enemies))
                    ++enemies
                }

            }
        }

        if(enemies > 0) {
            room.closeDoors()
        } else {
            room.openDoors()
        }

        room.alreadyCleared = enemies == 0 || cleared

        return room
    }


    private fun newEnemyMap(): Array<String> {
        val mutable: MutableList<String> = when(difficulty) {
            0 -> mutableListOf(
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "gold_skeleton",
                    "slime",
                    "slime",
                    "vampire",
                    "vampire",
                    "dark_knight"
            )

            1 -> mutableListOf(
                    "black_skeleton",
                    "skeleton",
                    "skeleton",
                    "gold_skeleton",
                    "wisp",
                    "wisp",
                    "vampire",
                    "vampire",
                    "dark_knight",
                    "dark_knight"
            )

            2 -> mutableListOf(
                    "black_skeleton",
                    "black_skeleton",
                    "gold_skeleton",
                    "gold_skeleton",
                    "wisp",
                    "wisp",
                    "vampire",
                    "vampire",
                    "dark_knight",
                    "dark_knight"
            )

            else -> mutableListOf(
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton",
                    "skeleton"
            )
        }

        Collections.shuffle(mutable)
        return mutable.toTypedArray()
    }


    private fun makeBoss(room: RoomState, pos: Tile, existingEnemies: Int): Enemy {
        val spawnPos = pos.offset(10, 4)
        return when(difficulty) {
            1 -> Demon(room, spawnPos, existingEnemies)
            2 -> WhiteKnight(room, spawnPos, existingEnemies)
            else -> Necromancer(room, spawnPos, existingEnemies)
        }
    }


    private fun makeEnemy(room: RoomState, pos: Tile, group: Int, existingEnemies: Int): Enemy {
        val name = enemyMap[group]
        val spawnPos = pos.offset(10, 4)
        return when(name) {
            "skeleton" -> Skeleton(room, spawnPos, existingEnemies)
            "gold_skeleton" -> GoldSkeleton(room, spawnPos, existingEnemies)
            "black_skeleton" -> BlackSkeleton(room, spawnPos, existingEnemies)
            "slime" -> Slime(room, spawnPos, existingEnemies)
            "vampire" -> Vampire(room, spawnPos, existingEnemies)
            "dark_knight" -> DarkKnight(room, spawnPos, existingEnemies)
            "wisp" -> {
                if(RandomUtils.chance(0.5)) {
                    Wisp(room, spawnPos, existingEnemies)
                } else {
                    BlueWisp(room, spawnPos, existingEnemies)
                }
            }
            "necromancer" -> Necromancer(room, spawnPos, existingEnemies)
            "demon" -> Demon(room, spawnPos, existingEnemies)
            "white_knight" -> WhiteKnight(room, spawnPos, existingEnemies)

            else -> {
                println("Invalid name: $name")
                Skeleton(room, spawnPos, existingEnemies)
            }
        }
    }


    fun intValueOf(character: Char): Int = when(character) {
        '0' -> 0
        '1' -> 1
        '2' -> 2
        '3' -> 3
        '4' -> 4
        '5' -> 5
        '6' -> 6
        '7' -> 7
        '8' -> 8
        '9' -> 9
        else -> -1
    }

}