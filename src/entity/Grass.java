package entity;

import java.awt.*;

public class Grass extends Entity{
    public Grass(double grassX, double grassY) {
        setDefualtValues(grassX, grassY);
    }
    public void setDefualtValues(double grassX, double grassY) {
        hasCollision = false;
        x = grassX;
        y = grassY;
    }
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(124, 252, 0));
        g2.fillRect((int)Math.round(x), (int)Math.round(y), 5, 15);
    }
}
