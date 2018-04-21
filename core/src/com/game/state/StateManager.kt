package com.game.state

import com.game.graphics.Fonts
import com.game.graphics.GameCanvas
import com.game.graphics.Textures

object StateManager {

    private var current: State? = null
    private var queued: State? = null

    val gameLayer: GameCanvas = GameCanvas(0.5f, 0)
    val hudLayer: GameCanvas = GameCanvas(1.0f, 1)

    fun create() {

    }

    fun tick() {
        if(queued != null) {
            current = queued
            queued = null
        }

        current?.update()
    }

    fun destroy() {
        gameLayer.dispose()
        hudLayer.dispose()
        Textures.dispose()
        Fonts.dispose()
    }
}