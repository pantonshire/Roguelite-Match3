package com.game.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport



class Renderer(var zoom: Float) {

    private val sprites: SpriteBatch = SpriteBatch()
    private val shapes: ShapeRenderer = ShapeRenderer()
    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: Viewport

    private var cameraMoved: Boolean = false

    init {
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        viewport = ScreenViewport(camera)
    }

    fun beginSprites() {
        if(cameraMoved) {
            camera.update()
            sprites.projectionMatrix = camera.combined
            shapes.projectionMatrix = camera.combined
            cameraMoved = false
        }

        sprites.begin()
    }

    fun endBatch() {
        sprites.end()
    }

    fun beginShapes() {
        shapes.begin()
    }

    fun endShapes() {
        shapes.end()
    }
}