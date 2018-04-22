package com.game.maths

enum class Direction(val x: Int, val y: Int) {

    NORTH   (0, 1),
    EAST    (1, 0),
    SOUTH   (0, -1),
    WEST    (-1, 0);

    fun angle(): Angle = Angle(when(this) {
        NORTH -> 0.0
        WEST -> Const.TAU * 0.25
        SOUTH -> Const.TAU * 0.5
        else -> Const.TAU * 0.75
    })

    fun opposite(): Direction = when(this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        else -> EAST
    }
}