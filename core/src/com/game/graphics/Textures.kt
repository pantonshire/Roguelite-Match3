package com.game.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException

object Textures: Disposable {

    private val textures: MutableMap<String, Texture> = mutableMapOf()
    private val error: Texture = Texture("textures/error.png")

    private fun load(name: String) {
        try {
            val texture = Texture("textures/$name.png")
            textures[name] = texture
        } catch(exception: GdxRuntimeException) {
            println("[ALERT] Error loading \"textures/$name.png\"")
        }
    }

    fun get(name: String): Texture {
        if(!textures.containsKey(name)) {
            load(name)
        }
        return textures[name] ?: error
    }

    override fun dispose() {
        textures.asSequence().forEach { it.value.dispose() }
        textures.clear()
    }
}