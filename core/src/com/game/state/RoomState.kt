package com.game.state

import com.game.graphics.GameCanvas
import tilemap.TileMap

class RoomState: State() {

    val tiles: TileMap = TileMap(50, 50, 24, "tiles", 5)

    override fun update() {

    }

    override fun drawGame(canvas: GameCanvas) {
        tiles.draw(canvas)
    }

    override fun drawHUD(canvas: GameCanvas) {

    }
}