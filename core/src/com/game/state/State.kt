package com.game.state

import com.game.graphics.GameCanvas

abstract class State {

    abstract fun update()

    fun draw(pass: Int, canvas: GameCanvas) {
        when(pass) {
            0 -> drawGame(canvas)
            1 -> drawHUD(canvas)
        }
    }

    abstract fun drawGame(canvas: GameCanvas)

    abstract fun drawHUD(canvas: GameCanvas)

}