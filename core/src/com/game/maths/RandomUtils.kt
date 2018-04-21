package com.game.maths

import java.util.*

object RandomUtils {

    val rng: Random = Random()

    fun randDirection(): Direction = Direction.values()[rng.nextInt(Direction.values().size)]

    fun chance(chance: Double): Boolean = rng.nextDouble() < chance

    fun flipCoin(): Boolean = rng.nextBoolean()

}