package entity;

import java.awt.*;

public class Tree extends Entity{
    public Tree() {
        setDefualtValues();
    }
    public void setDefualtValues() {
        x = 100;
        y = 100;
    }
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(126, 90, 60));
        g2.fillRect((int)Math.round(x), (int)Math.round(y), 40, 40);
    }
}
