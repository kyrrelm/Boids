package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Vector;

public class Swarm extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	ArrayList<Agent> agents;
	OrthographicCamera cam;
	static final int WIDTH = 2000;
	static final int HEIGHT = 2000;
	ShapeRenderer shapeRenderer;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		cam = new OrthographicCamera(800, 800*(h/w));
		cam.position.set(400, 400, 0);
		cam.update();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		agents = new ArrayList<Agent>();
		for (int i = 0; i < 200; i++) {
			agents.add(new Agent(400, 400+i, agents));
		}
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glLineWidth(3);
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.BROWN);
		shapeRenderer.line(0,0,HEIGHT,0);
		shapeRenderer.line(0,0,0,WIDTH);
		shapeRenderer.line(HEIGHT,0,HEIGHT,WIDTH);
		shapeRenderer.line(0,WIDTH,HEIGHT,WIDTH);
		shapeRenderer.end();

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


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		System.out.println("x: "+screenX+" y: "+screenY);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		cam.zoom += (float)amount/20;
		System.out.println("Cam zoom: "+cam.zoom);
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.println("mouse click");
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
}
