package entity;

import main.Viewport;

import java.awt.*;

public abstract class Entity {
    public double x, y;
    public boolean hasCollision;

    public void updateOnFrame(double timePassedSecs) {}

    public abstract void draw(Graphics2D g2);
}
