package com.game.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException

object SFX {

    private val sounds: MutableMap<String, Sound> = mutableMapOf()
    private var enabled: Boolean = true

    init {
        if(!enabled) {
            println("[ALERT] Sound effects disabled")
        }
    }

    fun load(name: String) {
        try {
            val sound = Gdx.audio.newSound(Gdx.files.internal("sfx/$name.wav"))
            sounds[name] = sound
        } catch(exception: GdxRuntimeException) {
            println("[ALERT] Error loading \"sfx/$name.wav\"")
        }
    }

    fun play(name: String, volume: Float = 1f, pitch: Float = 1f, pan: Float = 0f) {
        if(enabled) {
            if(sounds.containsKey(name)) {
                sounds[name]?.play(volume, pitch, pan)
            }
        }
    }

    fun dispose() {
        sounds.asSequence().forEach { it.value.dispose() }
        sounds.clear()
    }
}