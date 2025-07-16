package entity;

import main.Viewport;

import java.awt.*;

import static java.lang.Math.round;

public class Rock extends Entity{
    public Rock(double rockX, double rockY) {
        x = rockX;
        y = rockY;
        hasCollision = true;
    }

    @Override
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
        g2.setColor(Color.gray);
        g2.fillOval(
                (int) round(((x - Viewport.viewport.getX()) * scale) + Viewport.viewport.getXOffset()),
                (int) round(((y - Viewport.viewport.getY()) * scale) + Viewport.viewport.getYOffset()),
                (int)round(40 * scale),
                (int)round(40 * scale));
    }
}
