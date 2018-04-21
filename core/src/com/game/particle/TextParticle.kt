package com.game.particle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.game.graphics.Fonts
import com.game.graphics.GameCanvas
import com.game.maths.Vector

class TextParticle(position: Vector, velocity: Vector, val time: Int, val text: String, val font: String, val size: Int, val colour: Color): Particle(position, velocity) {

    val layout: GlyphLayout = GlyphLayout(Fonts.get(font, size), text)

    override fun shouldRemove(): Boolean = ticksExisted > time

    override fun draw(canvas: GameCanvas) {
        canvas.drawText(text, position.xf() - layout.width / 2f, position.yf() + layout.height / 2f, font, size, colour)
    }
}