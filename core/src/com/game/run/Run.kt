package com.game.run

import com.game.maths.Direction
import com.game.maths.RandomUtils
import com.game.maths.Tile
import com.game.state.RoomData
import com.game.state.RoomTemplates
import com.game.state.StateManager

class Run {

    companion object {

        var current = Run()

        fun newRun() {
            current = Run()
        }
    }

    var floor: Array<Array<RoomData?>> = generateFloor()
    var currentRoom: RoomData = firstRoom()
    val currentRoomPos: Tile = Tile(0, 0)
    var difficulty = 0

    var maxHealth: Int      = 3
    var health: Int         = 3
    var movements: Int      = 8
    var attacks: Int        = 2

    fun firstRoom(): RoomData = floor[0][0]!!

    fun travel(direction: Direction) {
        val newPos = currentRoomPos.offset(direction)
        if(newPos.x >= 0 && newPos.y >= 0) {
            val newRoom = floor[newPos.x][newPos.y]
            if(newRoom != null) {
                floor[currentRoomPos.x][currentRoomPos.y]?.cleared = true
                currentRoomPos.set(newPos.x, newPos.y)
                currentRoom = newRoom
                StateManager.queueRoom(newRoom, direction)
            }
        }
    }

    fun nextFloor() {
        ++difficulty
        floor = generateFloor()
        currentRoom = firstRoom()
        StateManager.queueRoom(currentRoom, Direction.NORTH)
    }

    fun loseHeart() {
        --health
    }

    fun addHeart() {
        if(health < maxHealth) {
            ++health
        }
    }

    fun fullyHeal() {
        health = maxHealth
    }

    fun addMaxHealth() {
        ++maxHealth
        ++health
    }

    private fun generateFloor(): Array<Array<RoomData?>> {
        val newFloor: Array<Array<RoomData?>> = Array(10, { Array(10, { null as RoomData? }) })
        newFloor[0][0] = RoomData(difficulty, false, false, false, false, RoomTemplates.startRoom())
        val r = Tile(0, 0)
        var rooms = 0

        while(rooms < 5) {
            r.add(RandomUtils.randDirection())

            if(r.x > 8) {
                r.x = 8
            } else if(r.x < 0) {
                r.x = 0
            }

            if(r.y > 8) {
                r.y = 8
            } else if(r.y < 0) {
                r.y = 0
            }

            if(newFloor[r.x][r.y] == null) {

                ++rooms
                val room = RoomData(difficulty, false, false, false, false, RoomTemplates.combatRoom(difficulty))
                newFloor[r.x][r.y] = room

                if(r.x > 0 && newFloor[r.x - 1][r.y] != null) {
                    newFloor[r.x - 1][r.y]?.east = true
                    room.west = true
                }
                if(newFloor[r.x + 1][r.y] != null) {
                    newFloor[r.x + 1][r.y]?.west = true
                    room.east = true
                }
                if(r.y > 0 && newFloor[r.x][r.y - 1] != null) {
                    newFloor[r.x][r.y - 1]?.north = true
                    room.south = true
                }
                if(newFloor[r.x][r.y + 1] != null) {
                    newFloor[r.x][r.y + 1]?.south = true
                    room.north = true
                }

            }
        }

        val furthestRoom = Tile(0, 0)
        var furthestDist = 0
        for(x in 0 until newFloor.size) {
            for(y in 0 until newFloor[x].size) {
                val room = newFloor[x][y]
                if(room != null && x + y > furthestDist) {
                    furthestDist = x + y
                    furthestRoom.set(x, y)
                }
            }
        }

        val vertical = RandomUtils.flipCoin()
        val bossRoom = RoomData(difficulty, false, false, vertical, !vertical, RoomTemplates.bossRoom(difficulty), true)
        if(vertical) {
            newFloor[furthestRoom.x][furthestRoom.y + 1] = bossRoom
            newFloor[furthestRoom.x][furthestRoom.y]?.north = true
        } else {
            newFloor[furthestRoom.x + 1][furthestRoom.y] = bossRoom
            newFloor[furthestRoom.x][furthestRoom.y]?.east = true
        }

        return newFloor
    }

}