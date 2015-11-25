package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Kyrre on 22/11/2015.
 */
public class Agent extends Sprite{

    public static final float MAX_TURNING_ANGLE = 5;
    public static final float MAX_SPEED = 5;

    float turningAngle = 0;
    private Vector2 velocity;
    private ArrayList<Agent> agents;
    private Vector2 newDir;

    public Agent(ArrayList<Agent> agents) {
        super(new Texture("arrow.png"));
        this.agents = agents;
        this.setPosition(400, 400);
        velocity = new Vector2(1,0);
        this.setRotation(velocity.angle());

        Vector2 a = new Vector2(0,-1);
        Vector2 b = new Vector2(-1,0);
        System.out.println(a.angle(b));
    }

    public void update(){
        velocity.nor();
        avoidWall();
        newDir = new Vector2(velocity.x, velocity.y);
        avoidCollision(newDir);
        moveRandom(newDir);
        enforceAgilityLimit(newDir);
    }

    private void enforceAgilityLimit(Vector2 newDir) {
        float difAngle = velocity.angle(newDir);
        if (difAngle>MAX_TURNING_ANGLE){
            newDir.setAngle(velocity.angle()+MAX_TURNING_ANGLE);
        }else if (difAngle<-MAX_TURNING_ANGLE){
            newDir.setAngle(velocity.angle()-MAX_TURNING_ANGLE);
        }
        newDir.setLength(Math.min(MAX_SPEED,newDir.len()));
    }

    private void avoidCollision(Vector2 newDir) {
        for (Agent a: agents){
            if (a.equals(this))
                continue;
            Vector2 avoidVector = new Vector2((this.getCenterX()-a.getCenterX()),this.getCenterY()-a.getCenterY());
            if (avoidVector.len() < 40){
                avoidVector.nor();
                newDir.add(avoidVector);
            }
        }
    }

    private void moveRandom(Vector2 newDir) {
        float change = (float) (Math.random()-0.5);
        if (turningAngle > 0){
            change += 0.49;
        }else if (turningAngle < 0){
            change -= 0.49;
        }
        turningAngle += change;
        turningAngle = Math.max(change,-MAX_TURNING_ANGLE);
        turningAngle = Math.min(change,MAX_TURNING_ANGLE);
        newDir.setAngle((newDir.angle()+turningAngle));
    }

    private void avoidWall() {
        float boarderSize = 70;
        if (getCenterX()<boarderSize){
            velocity.x += (boarderSize-getCenterX())/500;
        }
        if(getCenterX()>Gdx.graphics.getWidth()-boarderSize){
            velocity.x -= (boarderSize-(Gdx.graphics.getWidth()-getCenterX()))/500;
        }
        if (getCenterY()<boarderSize){
            velocity.y += (boarderSize-getCenterY())/500;
        }
        if(getCenterY()>Gdx.graphics.getHeight()-boarderSize){
            velocity.y -= (boarderSize-(Gdx.graphics.getHeight()-getCenterY()))/500;
        }
    }

    private float getCenterX(){
        return getX()+(getWidth()/2);
    }
    private float getCenterY(){
        return getY()+(getHeight()/2);
    }

    public void move(){
        velocity = newDir;
        this.setRotation(velocity.angle());
        this.setPosition(getX()+velocity.x, getY()+velocity.y);
    }
}
