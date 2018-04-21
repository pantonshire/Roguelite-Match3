package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.game.state.StateManager;

public class Main extends ApplicationAdapter {

    private StateManager stateManager = StateManager.INSTANCE;

	public void create() {
        stateManager.create();
	}

	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateManager.tick();
	}

	public void dispose() {
        stateManager.destroy();
	}
}
