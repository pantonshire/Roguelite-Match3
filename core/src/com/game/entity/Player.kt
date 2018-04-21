package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.maths.Tile
import com.game.run.Run
import com.game.state.RoomState

class Player(room: RoomState, pos: Tile): Entity(room, pos, 10.0) {

    val sprite: TextureRegion = TextureRegion(Textures.get("hero"))
    var movesLeft: Int = 0

    override fun act(): Boolean {
        if(movesLeft > 0 &&
                (Gdx.input.isKeyJustPressed(Input.Keys.W) && move(Direction.NORTH)) ||
                (Gdx.input.isKeyJustPressed(Input.Keys.A) && move(Direction.WEST)) ||
                (Gdx.input.isKeyJustPressed(Input.Keys.S) && move(Direction.SOUTH)) ||
                (Gdx.input.isKeyJustPressed(Input.Keys.D) && move(Direction.EAST))
        ) {
            --movesLeft
            println("$movesLeft moves left")
            return true
        }

        return false
    }

    override fun draw(canvas: GameCanvas) {
        val drawPos = drawPos()
        canvas.draw(sprite, drawPos.xf(), drawPos.yf())
    }

    override fun startTurn() {
        movesLeft = Run.current.movements
    }

    override fun endTurn() {

    }

    override fun isFinished(): Boolean {
        return movesLeft == 0
    }
}