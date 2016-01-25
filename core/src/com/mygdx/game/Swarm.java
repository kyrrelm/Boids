package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class Swarm extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	SpriteBatch hudBatch;
	ArrayList<Agent> agents;
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	OrthographicCamera cam;
	static final int WIDTH = 2000;
	static final int HEIGHT = 2000;
	ShapeRenderer shapeRenderer;
	Sprite preyButton;
	Sprite obstacleButton;
	Sprite clearObstacleButton;

	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		cam = new OrthographicCamera(800, 800*(h/w));
		cam.position.set(300, 300, 0);
		cam.update();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		agents = new ArrayList<Agent>();
		for (int i = 0; i < 10; i++) {
			//agents.add(new Agent(300, 300+i, agents, obstacles));
		}
		Gdx.input.setInputProcessor(this);
		preyButton = new Sprite(new Texture("arrow.png"));
		preyButton.setPosition(10,10);
		obstacleButton = new Sprite(new Texture("arrow.png"));
		obstacleButton.setPosition(50,10);
		clearObstacleButton = new Sprite(new Texture("arrow.png"));
		clearObstacleButton.setPosition(90,10);
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
		shapeRenderer.line(0,0,WIDTH,0);
		shapeRenderer.line(0,0,0,HEIGHT);
		shapeRenderer.line(WIDTH,0,WIDTH,HEIGHT);
		shapeRenderer.line(0,HEIGHT,WIDTH,HEIGHT);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		for (Obstacle o: obstacles) {
			shapeRenderer.circle(o.getX(),o.getY(),o.getRadius());
		}
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

		hudBatch.begin();
		preyButton.draw(hudBatch, 1F);
		obstacleButton.draw(hudBatch, 1F);
		clearObstacleButton.draw(hudBatch, 1F);
		hudBatch.end();

	}

	Vector3 tp = new Vector3();
	boolean rightButtonHold;
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (rightButtonHold){
			System.out.println("x: "+screenX+" y: "+screenY);
		}
		return false;
	}

	private enum ButtonType {
		PREY, OBSTACLE
	}

	private ButtonType selectedButton = ButtonType.OBSTACLE;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT){
			System.out.println("X: "+screenX);
			System.out.println("Y: "+screenY);
			Vector3 tp2 = new Vector3();
			cam.unproject(tp2.set(screenX, screenY, 0));
			System.out.println("TP X:" + tp2.x);
			System.out.println("TP Y:" + tp2.y);
			System.out.println("Button X:" + preyButton.getX());
			System.out.println("Button Y:" + preyButton.getY());
			if(preyButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
				selectedButton = ButtonType.PREY;
				return true;
			}
			if(obstacleButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
				selectedButton = ButtonType.OBSTACLE;
				return true;
			}
			if(clearObstacleButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
				obstacles.clear();
				return true;
			}
			switch (selectedButton){
				case PREY:{
					agents.add(new Agent(tp2.x, tp2.y, agents, obstacles));
					return true;
				}
				case OBSTACLE:{
					obstacles.add(new Obstacle(tp2.x, tp2.y, (float)(10+(30*Math.random()))));
					return true;
				}
			}
		}
		if (button == Input.Buttons.RIGHT){
			cam.unproject(tp.set(screenX, screenY, 0));
			oldMouseX = tp.x;
			oldMouseY = tp.y;
			rightButtonHold = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		rightButtonHold = false;
		return false;
	}

	float oldMouseX = 0;
	float oldMouseY = 0;
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (rightButtonHold){
			cam.unproject(tp.set(screenX, screenY, 0));
			System.out.println("tp x: "+(oldMouseX-tp.x));
			System.out.println("tp y: "+(oldMouseY-tp.y));
			cam.translate(oldMouseX-tp.x, oldMouseY-tp.y, 0);
			oldMouseX = tp.x;
			oldMouseY = tp.y;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		cam.zoom += (float)amount/5;
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
}
