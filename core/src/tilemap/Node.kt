package tilemap

import com.game.maths.Tile

class Node(val pos: Tile, val h: Float) {

    var g: Float = -1f
    var previous: Node? = null

    fun fScore(): Float = g + h
}