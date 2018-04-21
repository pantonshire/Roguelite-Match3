package com.game.graphics

class AnimationSequence(val name: String,
                        val frameWidth: Int, val frameHeight: Int,
                        val numFrames: Int, val delay: Int,
                        private val loopInfinitely: Boolean = true, private val numLoops: Int = 1) {

    var y: Int = 0
    var linkedSequences: MutableSet<Int> = mutableSetOf()

    fun shouldLoop(totalLoops: Int): Boolean {
        return loopInfinitely || totalLoops < numLoops
    }
}