package entity;

import main.Viewport;

import java.awt.*;

import static java.lang.Math.round;

public class Tree extends Entity{
    boolean isDestroyed;
    float health;
    public Tree(double treeX, double treeY) {
        x = treeX;
        y = treeY;
        hasCollision = true;
        isDestroyed = false;
        health = 1000;
    }

    @Override
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
        g2.setColor(new Color(126, 90, 60));
        g2.fillOval(
                (int) round(((x - Viewport.viewport.getX()) * scale) + Viewport.viewport.getXOffset()),
                (int) round(((y - Viewport.viewport.getY()) * scale) + Viewport.viewport.getYOffset()),
                (int)round(20 * scale),
                (int)round(20 * scale));
    }
}
