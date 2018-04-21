package com.game.state

import com.badlogic.gdx.graphics.Color
import com.game.graphics.Fonts
import com.game.graphics.GameCanvas
import com.game.graphics.Textures

object StateManager {

    val GAME_LAYER = 0
    val HUD_LAYER = 1

    private var current: State? = null
    private var queued: State? = null

    private var layers: Array<GameCanvas> = arrayOf()

    fun create() {
        layers = arrayOf(
                GameCanvas(0.5f),   //Game canvas
                GameCanvas(1.0f)    //HUD canvas
        )

        layers[0].setCameraPosition(320f, 200f)

        current = RoomState()
    }

    fun tick() {
        if(queued != null) {
            current = queued
            queued = null
        }

        current?.update()

        for(layerId in 0 until layers.size) {
            layers[layerId].beginSprites()
            current?.draw(layerId, layers[layerId])
            layers[layerId].finishSprites()
        }

    }

    fun destroy() {
        layers.asSequence().forEach { it.dispose() }
        Textures.dispose()
        Fonts.dispose()
    }

    fun getCanvas(id: Int): GameCanvas = layers[id]

}