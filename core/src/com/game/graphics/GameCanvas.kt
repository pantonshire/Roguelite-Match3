package com.game.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.game.maths.Angle
import com.game.maths.Vector


class GameCanvas(zoom: Float, val id: Int): Disposable {

    val viewport: ScreenViewport
    val spriteBatch: SpriteBatch = SpriteBatch()
    val refreshColour: Color = spriteBatch.color.cpy()
    val camera: OrthographicCamera = OrthographicCamera()
    var cameraMoved: Boolean = true
    var width: Int
    var height: Int
    var left: Float = 0f
    var right: Float = 0f
    var top: Float = 0f
    var bottom: Float = 0f

    init {
        width = Gdx.graphics.width
        height = Gdx.graphics.height
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        camera.zoom = zoom
        viewport = ScreenViewport(camera)
        updateDimensions(width, height)
    }

    fun beginDrawing() {
        if(cameraMoved) {
            camera.update()
            spriteBatch.projectionMatrix = camera.combined
            cameraMoved = false
        }

        spriteBatch.begin()
        val scissors = Rectangle()
        val clipBounds = Rectangle(left, bottom, (width / 2).toFloat(), (height / 2).toFloat())
        ScissorStack.calculateScissors(camera, spriteBatch.transformMatrix, clipBounds, scissors)
        ScissorStack.pushScissors(scissors)
    }

    fun finishDrawing() {
        spriteBatch.flush()
        ScissorStack.popScissors()
        spriteBatch.end()
    }

    fun draw(texture: Texture,
             x: Float, y: Float,
             originOffsetX: Float = 0f, originOffsetY: Float = 0f,
             scaleX: Float = 1f, scaleY: Float = 1f,
             rotation: Angle = Angle(),
             flipX: Boolean = false, flipY: Boolean = false) {
        val width: Int = texture.width
        val height: Int = texture.height
        if(onScreen(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())) {
            val hWidth: Float = width / 2f
            val hHeight: Float = height / 2f
            spriteBatch.draw(texture, x - hWidth, y - hHeight, originOffsetX + hWidth, originOffsetY + hHeight, width.toFloat(), height.toFloat(), scaleX, scaleY, rotation.deg().toFloat(), 0, 0, width, height, flipX, flipY)
        }
    }

    fun draw(texture: TextureRegion,
             x: Float, y: Float,
             originOffsetX: Float = 0f, originOffsetY: Float = 0f,
             scaleX: Float = 1f, scaleY: Float = 1f,
             rotation: Angle = Angle()) {
        val width: Float = texture.regionWidth.toFloat()
        val height: Float = texture.regionHeight.toFloat()
        if(onScreen(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())) {
            val hWidth: Float = width / 2f
            val hHeight: Float = height / 2f
            spriteBatch.draw(texture, x - hWidth, y - hHeight, originOffsetX + hWidth, originOffsetY + hHeight, width, height, scaleX, scaleY, rotation.deg().toFloat())
        }
    }

    fun fastDrawGrid(texture: TextureRegion, x: Int, y: Int) {
        spriteBatch.draw(texture, x.toFloat(), y.toFloat())
    }

    fun fastDraw(texture: Texture, x: Float, y: Float) {
        spriteBatch.draw(texture, x, y)
    }

    fun draw(animation: Animation,
             x: Float, y: Float,
             originOffsetX: Float = 0f, originOffsetY: Float = 0f,
             scaleX: Float = 1f, scaleY: Float = 1f,
             rotation: Angle = Angle()) {
        animation.updateTextureRegion()
        draw(animation.textureRegion, x, y, originOffsetX, originOffsetY, scaleX, scaleY, rotation)
    }

    fun drawText(text: String, x: Float, y: Float, fontName: String, size: Int, red: Float = 0f, green: Float = 0f, blue: Float = 0f, alpha: Float = 1f) {
        val font = Fonts.get(fontName, size)
        font.setColor(red, green, blue, alpha)
        font.draw(spriteBatch, text, x, y)
    }

    fun updateDimensions(newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight
        viewport.update(width, height)
        setCameraPosition((width / 2).toFloat(), (height / 2).toFloat())
        updateScreenBounds()
    }

    fun setCameraPosition(newX: Float, newY: Float) {
        if(camera.position.x != newX || camera.position.y != newY) {
            camera.position.set(newX, newY, 0f)
            updateScreenBounds()
            cameraMoved = true
        }
    }

    private fun updateScreenBounds() {
        val screenWidth = width * camera.zoom
        val screenHeight = height * camera.zoom
        right = if(id % 2 == 0) camera.position.x else camera.position.x + (screenWidth / 2)
        left = right - (screenWidth / 2)
        top = if(id > 1) camera.position.y else camera.position.y + (screenHeight / 2)
        bottom = top - (screenHeight / 2)
    }

    fun setCameraPosition(position: Vector) = setCameraPosition(position.x.toFloat(), position.y.toFloat())

    fun onScreen(x: Double, y: Double, width: Double, height: Double): Boolean =
            x + width >= left && x - width <= right && y + height >= bottom && y - height <= top

    fun onScreen(pos: Vector, width: Double, height: Double): Boolean = onScreen(pos.x, pos.y, width, height)

    fun tint(red: Float = 1f, green: Float = 1f, blue: Float = 1f, alpha: Float = 1f) {
        spriteBatch.color = Color(red, green, blue, alpha)
    }

    fun removeTint() {
        spriteBatch.color = refreshColour
    }

    override fun dispose() {
        spriteBatch.dispose()
    }
}