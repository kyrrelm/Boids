package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * Created by Kyrre on 04/2/2016.
 */
public class Predator extends Agent {

    public Predator(float x, float y, ArrayList<Agent> agents, ArrayList<Obstacle> obstacles) {
        super(x, y, agents, obstacles);
        setTexture(new Texture("predator.png"));
    }
}
