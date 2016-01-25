package com.mygdx.game;

/**
 * Created by Kyrre on 25/1/2016.
 */
public class Obstacle {
    private float x;
    private float y;
    private float radius;

    public Obstacle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}
