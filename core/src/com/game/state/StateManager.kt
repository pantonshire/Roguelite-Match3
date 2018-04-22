package com.game.state

import com.game.graphics.Fonts
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile

object StateManager {

    val GAME_LAYER = 0
    val HUD_LAYER = 1

    private var current: State? = null
    private var queued: State? = null

    private var layers: Array<GameCanvas> = arrayOf()

    fun create() {
        layers = arrayOf(
                GameCanvas(0.5f),   //Game canvas
                GameCanvas(0.5f)    //HUD canvas
        )

        layers[0].setCameraPosition(384f, 240f)

        current = RoomData(0, true, true, false, false,
                arrayOf(
                        Pair(   'w',    Tile(5, 5)     ),
                        Pair(   'w',    Tile(5, 6)     ),
                        Pair(   'w',    Tile(6, 5)     ),
                        Pair(   'w',    Tile(6, 6)     ),
                        Pair(   '0',    Tile(4, 5)     ),
                        Pair(   '0',    Tile(7, 5)     ),
                        Pair(   '0',    Tile(5, 7)     ),
                        Pair(   '1',    Tile(1, 1)     ),
                        Pair(   '1',    Tile(1, 10)     ),
                        Pair(   '1',    Tile(10,  1)     ),
                        Pair(   '1',    Tile(10,  10)     )
                )
        ).makeRoom(Direction.WEST)
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