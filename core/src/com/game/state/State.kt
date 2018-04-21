package com.game.state

import com.game.graphics.GameCanvas

abstract class State {

    abstract fun update()

    abstract fun draw(pass: Int, canvas: GameCanvas)

}