package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

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
	Sprite clearAgentButton;
	Sprite obstacleButton;
	Sprite clearObstacleButton;
	Skin skin;
	Stage stage;

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

		stage = new Stage();

		Gdx.input.setInputProcessor(stage);
		preyButton = new Sprite(new Texture("arrow.png"));
		preyButton.setPosition(10,10);
		clearAgentButton = new Sprite(new Texture("arrow.png"));
		clearAgentButton.setPosition(50,10);
		obstacleButton = new Sprite(new Texture("arrow.png"));
		obstacleButton.setPosition(90,10);
		clearObstacleButton = new Sprite(new Texture("arrow.png"));
		clearObstacleButton.setPosition(130,10);

		//skin = new Skin(Gdx.files.internal("data/uiskin32.json"));


		skin = new Skin();

		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		skin.add("default", new BitmapFont());

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton button = new TextButton("Click me!", skin);
		table.add(button);

		button.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				System.out.println("Clicked! Is checked: " + button.isChecked());
				button.setText("Good job!");
			}
		});

		// Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
		table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);



		stage.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float screenX, float screenY) {
				System.out.println(event);

				System.out.println("X: "+screenX);
				System.out.println("Y: "+screenY);
				Vector3 tp2 = new Vector3();
				cam.unproject(tp2.set(screenX, Gdx.graphics.getHeight()-screenY, 0));
				System.out.println("TP X:" + tp2.x);
				System.out.println("TP Y:" + tp2.y);
				System.out.println("Button X:" + preyButton.getX());
				System.out.println("Button Y:" + preyButton.getY());

				if(preyButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
					selectedButton = ButtonType.PREY;
				}
				if(clearAgentButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
					agents.clear();
				}
				if(obstacleButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
					selectedButton = ButtonType.OBSTACLE;
				}
				if(clearObstacleButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
					obstacles.clear();
				}
				switch (selectedButton){
					case PREY:{
						agents.add(new Agent(tp2.x, tp2.y, agents, obstacles));
						break;
					}
					case OBSTACLE:{
						obstacles.add(new Obstacle(tp2.x, tp2.y, (float)(10+(30*Math.random()))));
						break;
					}
				}
			}
//			@Override
//			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
//				System.out.println(event);
//				System.out.println(x);
//				System.out.println(y);
//				return true;
//			}
		});



		stage.addListener(new DragListener(){

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if (button == Input.Buttons.RIGHT){
					dragX = x;
					dragY = y;
					return true;
				}
				return false;
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				float dX = (float)(x-dragX)/(float)Gdx.graphics.getWidth();
				float dY = (float)(dragY-y)/(float)Gdx.graphics.getHeight();
				dragX = x;
				dragY = y;
				cam.position.add(-dX * 1000f, dY * 1000f, 0f);
				cam.update();
			}

		});

		stage.addListener(new InputListener() {
			@Override
			public boolean scrolled(InputEvent event, float x, float y, int amount){
				cam.zoom += (float)amount/5;
				return true;
			}
		});


	}
	private float dragX, dragY;
	Vector3 tp = new Vector3();
	float oldMouseX = 0;
	float oldMouseY = 0;

	@Override
	public void render () {
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();

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
		clearAgentButton.draw(hudBatch, 1F);
		obstacleButton.draw(hudBatch, 1F);
		clearObstacleButton.draw(hudBatch, 1F);
		hudBatch.end();

	}

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
			if(clearAgentButton.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight()-screenY)) {
				agents.clear();
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

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose () {
		stage.dispose();
		skin.dispose();
	}
}
