package entity;

import java.awt.*;

public class Rock extends Entity{
    public Rock(double rockX, double rockY) {
        setDefualtValues(rockX, rockY);
    }
    public void setDefualtValues(double rockX, double rockY) {
        x = rockX;
        y = rockY;
        hasCollision = true;
    }
    public void draw(Graphics2D g2) {
        g2.setColor(Color.gray);
        g2.fillOval((int)Math.round(x), (int)Math.round(y), 100, 100);
    }
}
