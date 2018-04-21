package com.game.graphics

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.game.maths.Angle
import com.game.maths.Vector

class Animation(spriteSheet: Texture, vararg animationSequences: AnimationSequence) {

    val textureRegion: TextureRegion = TextureRegion(spriteSheet)
    var originOffsetX: Float = 0.0f
    var originOffsetY: Float = 0.0f
    var scaleX: Float = 1.0f
    var scaleY: Float = 1.0f
    var rotation: Angle = Angle()

    val sequences: Array<out AnimationSequence> = animationSequences
    val sequenceNames: Map<String, Int>

    private var sequenceNo: Int = 0
    private var frameNo: Int = 0
    private var timer: Int
    private var loops: Int = 0
    private var dirty: Boolean = true

    init {
        val names: MutableMap<String, Int> = mutableMapOf()
        for(i in 0..(sequences.size - 1)) {
            names[sequences[i].name] = i
            if(i != 0) {
                val last = sequences[i - 1]
                sequences[i].y = last.y + last.frameHeight
            }
        }
        sequenceNames = names.toMap()
        timer = sequences[sequenceNo].delay

    }

    fun link(linkToName: String, vararg linkNames: String) {
        val linkTo: AnimationSequence = sequences[getSequenceIndex(linkToName)]
        linkNames.asSequence().filter { getSequenceIndex(it) != -1 }.mapTo(linkTo.linkedSequences) { getSequenceIndex(it) }
    }

    fun getSequenceIndex(name: String): Int = sequenceNames[name] ?: -1

    fun isPlayingSequence(name: String): Boolean = sequenceNo == getSequenceIndex(name)

    fun currentSequenceNo(): Int = sequenceNo

    fun isFinished(): Boolean = timer <= 1 && frameNo == sequences[sequenceNo].numFrames - 1

    fun resetSequence() {
        frameNo = 0
        loops = 0
        dirty = true
    }

    fun changeSpriteSheet(newSheet: Texture) {
        textureRegion.setRegion(newSheet)
        dirty = true
    }

    fun updateAnimation() {
        if(sequences[sequenceNo].delay > 0 && --timer <= 0) {
            timer = sequences[sequenceNo].delay
            dirty = true
            if(++frameNo >= sequences[sequenceNo].numFrames) {
                if(sequences[sequenceNo].shouldLoop(++loops)) {
                    frameNo = 0
                } else {
                    --frameNo
                }
            }
        }
    }

    fun setSequence(nextSequence: Int) {
        if(nextSequence != -1 && nextSequence != sequenceNo) {
            frameNo = when(nextSequence in sequences[sequenceNo].linkedSequences) {
                true -> minOf(frameNo, sequences[nextSequence].numFrames - 1)
                false -> 0
            }

            sequenceNo = nextSequence
            timer = sequences[sequenceNo].delay
            loops = 0
            dirty = true
        }
    }

    fun updateTextureRegion() {
        if(dirty) {
            val sequence = sequences[sequenceNo]
            textureRegion.setRegion(frameNo * sequence.frameWidth, sequence.y, sequence.frameWidth, sequence.frameHeight)
            dirty = false
        }
    }

    fun draw(canvas: GameCanvas, x: Float, y: Float) {
        if(dirty) {
            updateTextureRegion()
            dirty = false
        }

        canvas.draw(textureRegion, x, y, originOffsetX, originOffsetY, scaleX, scaleY, rotation)
    }

    fun draw(canvas: GameCanvas, position: Vector) = draw(canvas, position.x.toFloat(), position.y.toFloat())

}