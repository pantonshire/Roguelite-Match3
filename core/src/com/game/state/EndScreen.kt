package com.game.state

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.graphics.GameCanvas
import com.game.graphics.Textures
import com.game.maths.Direction
import com.game.run.Run

class EndScreen: State() {

    private val bg = TextureRegion(Textures.get("bg"))

    override fun update() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            StateManager.queue(MainMenu())
        }
    }

    override fun drawGame(canvas: GameCanvas) {}

    override fun drawHUD(canvas: GameCanvas) {
        for(x in 0 until 35) {
            for(y in 0 until 25) {
                canvas.drawTile(bg, 304 + x * 24, 640 - y * 24)
            }
        }

        canvas.drawText("Thank you for playing ROGUEMATCH!", 340f, 560f, "prstart", 16, Color.WHITE)
        canvas.drawText("What did you think? How could I", 340f, 520f, "prstart", 16, Color.WHITE)
        canvas.drawText("improve it? Please let me know with", 340f, 480f, "prstart", 16, Color.WHITE)
        canvas.drawText("a comment! :)", 340f, 440f, "prstart", 16, Color.WHITE)
        canvas.drawText("(Press space to return to the main", 340f, 400f, "prstart", 16, Color.WHITE)
        canvas.drawText("menu)", 340f, 360f, "prstart", 16, Color.WHITE)
    }
}