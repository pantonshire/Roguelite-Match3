package com.game.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

object Fonts: Disposable {

    private val fonts: MutableMap<String, BitmapFont> = mutableMapOf()
    private val default: BitmapFont = BitmapFont()

    fun load(name: String, size: Int) {
        try {
            val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/$name.ttf"))
            val parameter = FreeTypeFontParameter()
            parameter.size = size
            parameter.mono = true
            parameter.kerning = true
            val font = generator.generateFont(parameter)
            val referenceName = "$name-$size"
            fonts[referenceName] = font
        } catch(exception: GdxRuntimeException) {
            println("[ALERT] Error loading \"fonts/$name.ttf\"")
        }
    }

    fun get(font: String, size: Int): BitmapFont {
        return fonts["$font-$size"] ?: default
    }

    override fun dispose() {
        fonts.asSequence().forEach { it.value.dispose() }
        fonts.clear()
    }
}