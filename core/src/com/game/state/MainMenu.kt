package com.game.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.run.Run

class MainMenu: State() {

    private val bg = TextureRegion(Textures.get("bg"))

    private val options: Array<String> = arrayOf(
            "NEW GAME",
            "QUIT GAME"
    )

    var option: Int = 0

    override fun update() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            when(option) {
                0 -> {
                    Run.newRun()
                    StateManager.queueRoom(Run.current.firstRoom(), Direction.NORTH)
                }

                1 -> {
                    Gdx.app.exit()
                }
            }
        }

        else if(Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            --option
            if(option < 0) {
                option = options.size - 1
            }
        }

        else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            ++option
            if(option >= options.size) {
                option = 0
            }
        }
    }

    override fun drawGame(canvas: GameCanvas) {}

    override fun drawHUD(canvas: GameCanvas) {
        for(x in 0 until 35) {
            for(y in 0 until 25) {
                canvas.drawTile(bg, 304 + x * 24, 640 - y * 24)
            }
        }

        canvas.drawText("ROGUEMATCH", 560f, 560f, "prstart", 16, Color.WHITE)

        for(i in 0 until options.size) {
            canvas.drawText(options[i], 580f, 450f - 40 * i, "prstart", 16, Color.WHITE)
        }

        canvas.draw(Textures.get("pointer"), 550f, 445f - 40 * option)

        canvas.drawText("A game by Tom Panton for Ludum Dare 41: Combine two incompatible genres", 356f, 240f, "prstart", 8, Color.WHITE)
    }
}