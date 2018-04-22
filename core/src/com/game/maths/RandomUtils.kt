package com.game.maths

import java.util.*

object RandomUtils {

    val encouragement: Array<String> = arrayOf(
            "Wonderful!",
            "Great work!",
            "Good job!",
            "Splendid!",
            "Nice match!",
            "Excellent!",
            "You go!",
            "Nice one!",
            "Awesome!"
    )

    val rng: Random = Random()

    fun randDirection(): Direction = Direction.values()[rng.nextInt(Direction.values().size)]

    fun chance(chance: Double): Boolean = rng.nextDouble() < chance

    fun flipCoin(): Boolean = rng.nextBoolean()

    fun randRange(range: IntRange): Int = rng.nextInt(range.endInclusive - range.start + 1) + range.start

    fun randEncouragement(): String = encouragement[rng.nextInt(encouragement.size)]

}