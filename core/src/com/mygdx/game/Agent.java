package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Kyrre on 22/11/2015.
 */
public class Agent extends Sprite{

    public static final float MAX_TURNING_ANGLE = 5;
    public static final float MAX_SPEED = 2;

    float turningAngle = 0;
    private Vector2 velocity;
    private ArrayList<Agent> agents;
    private Vector2 newDir;

    public Agent(float x, float y, ArrayList<Agent> agents) {
        super(new Texture("arrow.png"));
        this.agents = agents;
        this.setPosition(x, y);
        velocity = new Vector2(1,0);
        this.setRotation(velocity.angle());
    }

    public void update(){
        velocity.nor();
        avoidWall();
        newDir = new Vector2(velocity.x, velocity.y);
        interact(newDir);
        //moveRandom(newDir);
        enforceAgilityLimit(newDir);
    }

    private void interact(Vector2 newDir) {
        //Vector2 cohesionVector = new Vector2();
        for (Agent a: agents){
            if (a.equals(this))
                continue;
            avoidCollision(newDir, a);
            alignment(newDir, a);
            cohesion(newDir, a);
        }
    }

    private void cohesion(Vector2 newDir, Agent a){
        Vector2 distVector = new Vector2((a.getCenterX()-this.getCenterX()),a.getCenterY()-this.getCenterY());
        float angleDif = velocity.angle(distVector);
        if (distVector.len() < 300 && angleDif > -90 && angleDif < 90){
            distVector.setLength((float) 0.003);
            newDir.add(distVector);
        }
    }

    private void alignment(Vector2 newDir, Agent a) {
        Vector2 distVector = new Vector2((this.getCenterX()-a.getCenterX()),this.getCenterY()-a.getCenterY());
        if (distVector.len() < 50){
            Vector2 tmp = new Vector2(a.getVelocity().x, a.getVelocity().y);
            tmp.setLength((float) 0.05);
            newDir.add(tmp);
        }
    }


    private void avoidCollision(Vector2 newDir, Agent a) {
        Vector2 avoidVector = new Vector2((this.getCenterX()-a.getCenterX()),this.getCenterY()-a.getCenterY());
        if (avoidVector.len() < 30){
            avoidVector.setLength(2);
            newDir.add(avoidVector);
        }

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

    public Vector2 getVelocity(){
        return velocity;
    }
}
