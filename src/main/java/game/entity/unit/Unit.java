package game.entity.unit;

import game.entity.Effects;
import game.entity.Entity;
import utils.numUtil;
import game.entity.players;

import java.util.ArrayList;

public abstract class Unit extends Entity {
    protected long hp;
    protected long armor;
    protected long speed;
    protected long turnSpeed;
    protected long damage;
    protected long attackSpeed;//in ticks
    protected long ticksUntilAttack;
    protected players player;
    protected ArrayList<Effects> effects;
    protected UnitState unitState;
    protected long targetX;
    protected long targetY;
    protected long targetDirection;
    protected Entity attackTarget;

    public double getX(){
        return numUtil.LTD(x);
    }
    public double getY(){
        return numUtil.LTD(y);
    }
    public void changeX(long change){
        x += change;
    }
    public void changeY(long change){
        y += change;
    }
    public double getLastX(){
        return numUtil.LTD(lastX);
    }
    public double getLastY(){
        return numUtil.LTD(lastY);
    }
    public abstract Unit copy();
}
