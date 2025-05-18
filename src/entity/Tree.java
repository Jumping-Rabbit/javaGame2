package entity;

import java.awt.*;

public class Tree extends Entity{
    public Tree(double treeX, double treeY) {
        setDefualtValues(treeX, treeY);
    }
    public void setDefualtValues(double treeX, double treeY) {
        x = treeX;
        y = treeY;
        hasCollision = true;
    }
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(126, 90, 60));
        g2.fillOval((int)Math.round(x), (int)Math.round(y), 50, 50);
    }
}
