package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Vector;

public class Swarm extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	ArrayList<Agent> agents;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		agents = new ArrayList<Agent>();
		for (int i = 0; i < 100; i++) {
			agents.add(new Agent(agents));
		}
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		for (Agent a: agents){
			a.update();
		}
		for (Agent a: agents){
			a.move();
			a.draw(batch);
		}

		batch.end();

	}
}
