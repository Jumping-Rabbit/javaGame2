package entity;

import main.Viewport;

import java.awt.*;

import static java.lang.Math.round;

public class Grass extends Entity {
    public Grass(double grassX, double grassY) {
        hasCollision = false;
        x = grassX;
        y = grassY;
    }

    @Override
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
        g2.setColor(new Color(124, 252, 0));
        g2.fillRect(
                (int)round(((x - Viewport.viewport.getX()) * scale) + Viewport.viewport.getXOffset()),
                (int)round(((y - Viewport.viewport.getY()) * scale) + Viewport.viewport.getYOffset()),
                (int)round(3 * scale),
                (int)round(6 * scale));
    }
}
