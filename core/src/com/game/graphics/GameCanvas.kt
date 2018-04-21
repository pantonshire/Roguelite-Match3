package com.game.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.game.maths.Angle
import com.game.maths.Vector


class GameCanvas(zoom: Float): Disposable {

    val viewport: ScreenViewport
    val sprites: SpriteBatch = SpriteBatch()
    val refreshColour: Color = sprites.color.cpy()
    val camera: OrthographicCamera = OrthographicCamera()

    var cameraMoved: Boolean = true
    var scrWidth: Int
    var scrHeight: Int


    init {
        scrWidth = Gdx.graphics.width
        scrHeight = Gdx.graphics.height
        camera.setToOrtho(false, scrWidth.toFloat(), scrHeight.toFloat())
        camera.zoom = zoom
        viewport = ScreenViewport(camera)
        updateDimensions(scrWidth, scrHeight)
    }


    fun beginSprites() {
        if(cameraMoved) {
            camera.update()
            sprites.projectionMatrix = camera.combined
            cameraMoved = false
        }

        sprites.begin()
    }


    fun finishSprites() {
        sprites.end()
    }


    fun draw(
             texture: Texture,
             x: Float,
             y: Float,
             originOffsetX: Float = 0f,
             originOffsetY: Float = 0f,
             scaleX: Float = 1f,
             scaleY: Float = 1f,
             rotation: Angle = Angle(),
             flipX: Boolean = false,
             flipY: Boolean = false
    ) {

        val width: Int = texture.width
        val height: Int = texture.height
        if(onScreen(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())) {
            val hWidth: Float = width / 2f
            val hHeight: Float = height / 2f
            sprites.draw(
                    texture,
                    x - hWidth,
                    y - hHeight,
                    originOffsetX + hWidth,
                    originOffsetY + hHeight,
                    width.toFloat(),
                    height.toFloat(),
                    scaleX, scaleY,
                    rotation.deg().toFloat(),
                    0,
                    0,
                    width,
                    height,
                    flipX,
                    flipY
            )
        }
    }


    fun draw(
             texture: TextureRegion,
             x: Float,
             y: Float,
             originOffsetX: Float = 0f,
             originOffsetY: Float = 0f,
             scaleX: Float = 1f,
             scaleY: Float = 1f,
             rotation: Angle = Angle()
    ) {

        val width: Float = texture.regionWidth.toFloat()
        val height: Float = texture.regionHeight.toFloat()
        if(onScreen(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())) {
            val hWidth: Float = width / 2f
            val hHeight: Float = height / 2f
            sprites.draw(texture,
                    x - hWidth,
                    y - hHeight,
                    originOffsetX + hWidth,
                    originOffsetY + hHeight,
                    width,
                    height,
                    scaleX,
                    scaleY,
                    rotation.deg().toFloat()
            )
        }
    }


    fun drawAnimation(animation: Animation,
                      x: Float,
                      y: Float,
                      originOffsetX: Float = 0f,
                      originOffsetY: Float = 0f,
                      scaleX: Float = 1f,
                      scaleY: Float = 1f,
                      rotation: Angle = Angle()
    ) {

        animation.updateTextureRegion()
        draw(animation.textureRegion, x, y, originOffsetX, originOffsetY, scaleX, scaleY, rotation)
    }


    fun fastDraw(texture: Texture, x: Float, y: Float) {
        sprites.draw(texture, x, y)
    }

    fun drawTile(texture: TextureRegion, x: Int, y: Int) {
        sprites.draw(texture, x.toFloat(), y.toFloat())
    }


    fun drawText(text: String, x: Float, y: Float, fontName: String, size: Int, colour: Color) {
        val font = Fonts.get(fontName, size)
        font.color = colour
        font.draw(sprites, text, x, y)
    }


    fun updateDimensions(newWidth: Int, newHeight: Int) {
        scrWidth = newWidth
        scrHeight = newHeight
        viewport.update(scrWidth, scrHeight)
        setCameraPosition((scrWidth / 2).toFloat(), (scrHeight / 2).toFloat())
    }


    fun setCameraPosition(newX: Float, newY: Float) {
        if(camera.position.x != newX || camera.position.y != newY) {
            camera.position.set(newX, newY, 0f)
            cameraMoved = true
        }
    }


    fun setCameraPosition(position: Vector) = setCameraPosition(position.x.toFloat(), position.y.toFloat())


    fun onScreen(x: Double, y: Double, width: Double, height: Double): Boolean =
                    x + width >= camera.position.x - scrWidth / 2 &&
                    x - width <= camera.position.x + scrWidth / 2 &&
                    y + height >= camera.position.y - scrHeight / 2 &&
                    y - height <= camera.position.y + scrHeight / 2

    fun onScreen(pos: Vector, width: Double, height: Double): Boolean = onScreen(pos.x, pos.y, width, height)


    fun tint(red: Float = 1f, green: Float = 1f, blue: Float = 1f, alpha: Float = 1f) {
        sprites.color = Color(red, green, blue, alpha)
    }


    fun tint(colour: Color) {
        sprites.color = colour
    }


    fun removeTint() {
        sprites.color = refreshColour
    }


    override fun dispose() {
        sprites.dispose()
    }

}