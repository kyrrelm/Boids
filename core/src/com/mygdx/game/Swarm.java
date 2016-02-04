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
import javafx.scene.control.Tab;

import java.util.ArrayList;

public class Swarm extends ApplicationAdapter{
	SpriteBatch batch;
	SpriteBatch hudBatch;
	ArrayList<Agent> agents;
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	OrthographicCamera cam;
	static final int WIDTH = 2000;
	static final int HEIGHT = 2000;
	ShapeRenderer shapeRenderer;
	Skin skin;
	Stage stage;
	private float dragX, dragY;
	private ButtonType selectedButton = ButtonType.OBSTACLE;
	private enum ButtonType {
		PREY, PREDATOR, OBSTACLE
	}

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

		agents.add(new Predator(300, 300, agents, obstacles));
		agents.add(new Predator(300, 400, agents, obstacles));
		agents.add(new Predator(300, 500, agents, obstacles));

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("data/uiskin32.json"));


		//skin = new Skin();

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
		//textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		table.left().bottom();

		Table row1 = new Table();
		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton preyButton = new TextButton(" Prey ", skin);
		row1.add(preyButton);
		final TextButton predatorButton = new TextButton(" Predator ", skin);
		row1.add(predatorButton);
		final TextButton removeAgentsButton = new TextButton(" Remove agents ", skin);
		row1.add(removeAgentsButton);
		final TextButton obstacleButton = new TextButton(" Obstacle ", skin);
		row1.add(obstacleButton);
		final TextButton removeObstacleButton = new TextButton(" Remove obstacle ", skin);
		row1.add(removeObstacleButton);
		table.add(row1).left().row();

		Table row2 = new Table();
		final Label separationLabel = new Label("Separation", skin);
		row2.add(separationLabel);
		final Slider separationSlider = new Slider(0, 500, 5, false, skin);
		row2.add(separationSlider).padRight(5);
		final Label alignmentLabel = new Label("Alignment", skin);
		row2.add(alignmentLabel);
		final Slider alignmentSlider = new Slider(0, 500, 5, false, skin);
		row2.add(alignmentSlider).padRight(5);
		final Label cohesionLabel = new Label("Separation", skin);
		row2.add(cohesionLabel);
		final Slider cohesionSlider = new Slider(0, 500, 5, false, skin);
		row2.add(cohesionSlider).padRight(5);
		table.add(row2);


		preyButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				selectedButton = ButtonType.PREY;
			}
		});
		predatorButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				selectedButton = ButtonType.PREDATOR;
			}
		});
		removeAgentsButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				agents.clear();
			}
		});
		obstacleButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				selectedButton = ButtonType.OBSTACLE;
			}
		});
		removeObstacleButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				obstacles.clear();
			}
		});

		stage.addListener(new DragListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if (button == Input.Buttons.LEFT){
					dragX = x;
					dragY = y;
					return true;
				}
				return false;
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				//ugly slider hax
				if (y<60)return;

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

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.RIGHT){
					Vector3 tp2 = new Vector3();
					cam.unproject(tp2.set(x, Gdx.graphics.getHeight()-y, 0));

					switch (selectedButton){
						case PREY:{
							agents.add(new Agent(tp2.x, tp2.y, agents, obstacles));
							break;
						}
						case PREDATOR:{
							agents.add(new Predator(tp2.x, tp2.y, agents, obstacles));
							break;
						}
						case OBSTACLE:{
							obstacles.add(new Obstacle(tp2.x, tp2.y, 20));
							break;
						}
					}
				}
				return true;
			}
		});


	}

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
