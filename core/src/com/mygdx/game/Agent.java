package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Kyrre on 22/11/2015.
 */
public class Agent extends Sprite{

    private static int id_enumerator = 1;

    public static final float MAX_TURNING_ANGLE = 5;
    public static final float MAX_SPEED = 2;
    private final ArrayList<Obstacle> obstacles;

    float turningAngle = 0;
    private Vector2 velocity;
    private ArrayList<Agent> agents;
    private Vector2 newDir;
    private int id;

    public Agent(float x, float y, ArrayList<Agent> agents, ArrayList<Obstacle> obstacles) {
        //TODO: spawn in random direction.
        super(new Texture("arrow.png"));
        this.id = id_enumerator++;
        this.agents = agents;
        this.obstacles = obstacles;
        this.setPosition(x, y);
        velocity = new Vector2(1,0);
        this.setRotation(velocity.angle());
        newDir = new Vector2();
    }

    public void update(){
        velocity.nor();
        avoidWall();
        newDir = new Vector2(velocity.x, velocity.y);
        if (!avoidObstacles(newDir)){
            if (!interact(newDir)){
                moveRandom(newDir);
            }
        }
        enforceAgilityLimit(newDir);
    }

    private boolean avoidObstacles(Vector2 newDir) {
        boolean avoiding = false;
        float speed = newDir.len();
        for (Obstacle o: obstacles){
            Vector2 avoidVector = new Vector2((o.getX()-this.getCenterX()),o.getY()-this.getCenterY());
            float angle = newDir.angle(avoidVector);
            if (angle<120 && angle>-120 && avoidVector.len()<o.getRadius()*3){
                Vector2 startPos = new Vector2(getCenterX(), getCenterY());
                Vector2 offset = newDir.cpy();
                offset.setLength(o.getRadius()+500);
                Vector2 endPos = new Vector2(getCenterX()+offset.x, getCenterY()+offset.y);
                float dist = Intersector.intersectSegmentCircleDisplace(startPos,endPos,new Vector2(o.getX(),o.getY()),o.getRadius()+5,avoidVector);

                if (!Float.isInfinite(dist)){
                    avoiding = true;
                    avoidVector.rotate(180);
                    avoidVector.setLength(dist*2);
                    newDir.add(avoidVector);
                }
            }
        }
        newDir.setLength(speed);
        return avoiding;
    }

    //TODO: kan flytte moveRandom inn hit
    private boolean interact(Vector2 newDir) {
        boolean moveRandom = false;
        for (Agent a: agents){
            if (a.equals(this))
                continue;
            if (avoidCollision(newDir, a) || alignment(newDir, a) || cohesion(newDir, a)){
                moveRandom = true;
            }
        }
        return moveRandom;
    }

    private boolean cohesion(Vector2 newDir, Agent a){
        Vector2 distVector = new Vector2((a.getCenterX()-this.getCenterX()),a.getCenterY()-this.getCenterY());
        boolean cohesion = false;
        float angleDif = velocity.angle(distVector);
        if (distVector.len() < 300 && angleDif > -90 && angleDif < 90){
            distVector.setLength((float) 0.003);
            newDir.add(distVector);
            cohesion = true;
        }
        return cohesion;
    }

    private boolean alignment(Vector2 newDir, Agent a) {
        Vector2 distVector = new Vector2((this.getCenterX()-a.getCenterX()),this.getCenterY()-a.getCenterY());
        boolean align = false;
        if (distVector.len() < 50){
            Vector2 tmp = new Vector2(a.getVelocity().x, a.getVelocity().y);
            tmp.setLength((float) 0.05);
            newDir.add(tmp);
            //align = true;
        }
        return align;
    }

    private boolean avoidCollision(Vector2 newDir, Agent a) {
        Vector2 avoidVector = new Vector2((this.getCenterX()-a.getCenterX()),this.getCenterY()-a.getCenterY());
        boolean avoid = false;
        if (avoidVector.len() < 30){
            avoidVector.setLength(2);
            newDir.add(avoidVector);
            avoid = true;
        }
        return avoid;
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
        if (getCenterX()<0){
            //velocity.x += (boarderSize-getCenterX())/500;
            this.setPosition(Swarm.WIDTH-20, getY());
        }
        if(getCenterX()>Swarm.WIDTH){
            //velocity.x -= (boarderSize-(Swarm.WIDTH-getCenterX()))/500;
            this.setPosition(-10, getY());
        }
        if (getCenterY()<0){
            //velocity.y += (boarderSize-getCenterY())/500;
            this.setPosition(getX(), Swarm.HEIGHT-20);
        }
        if(getCenterY()>Swarm.HEIGHT){
            //velocity.y -= (boarderSize-(Swarm.HEIGHT-getCenterY()))/500;
            this.setPosition(getX(), -10);

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
