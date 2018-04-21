package com.game.maths

class Tile(var x: Int, var y: Int) {

    fun offset(xAmount: Int, yAmount: Int): Tile = Tile(x + xAmount, y + yAmount)

    fun offset(direction: Direction): Tile = offset(direction.x, direction.y)

    fun offset(direction: Direction, distance: Int): Tile = offset(direction.x * distance, direction.y * distance)

    fun add(xAmount: Int, yAmount: Int): Tile {
        x += xAmount
        y += yAmount
        return this
    }

    fun add(direction: Direction): Tile {
        return add(direction.x, direction.y)
    }

    fun set(newX: Int, newY: Int): Tile {
        x = newX
        y = newY
        return this
    }
}