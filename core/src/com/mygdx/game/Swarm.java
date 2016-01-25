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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;

public class Swarm extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	SpriteBatch hudBatch;
	ArrayList<Agent> agents;
	OrthographicCamera cam;
	static final int WIDTH = 500;
	static final int HEIGHT = 500;
	ShapeRenderer shapeRenderer;
	Sprite testHud;
	
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
			agents.add(new Agent(300, 300+i, agents));
		}
		Gdx.input.setInputProcessor(this);
		testHud = new Sprite(new Texture("arrow.png"));
		testHud.setPosition(10,10);
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
		testHud.draw(hudBatch, 1F);
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
		PREY
	}

	private ButtonType selectedButton = ButtonType.PREY;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT){
			System.out.println("X: "+screenX);
			System.out.println("Y: "+screenY);
			Vector3 tp2 = new Vector3();
			cam.unproject(tp2.set(screenX, screenY, 0));
			System.out.println("TP X:" + tp2.x);
			System.out.println("TP Y:" + tp2.y);
			System.out.println("Button X:" + testHud.getX());
			System.out.println("Button Y:" + testHud.getY());
			if(testHud.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
				selectedButton = ButtonType.PREY;
				return true;
			}
			switch (selectedButton){
				case PREY:{
					agents.add(new Agent(tp2.x, tp2.y, agents));
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
