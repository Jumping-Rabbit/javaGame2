package entity;

import main.KeyHandler;
import main.MouseHandler;
import main.Viewport;

import java.awt.*;

import static java.lang.Math.*;
import static main.GameData.MAP_HEIGHT;
import static main.GameData.MAP_WIDTH;

public class Player extends Entity {
    KeyHandler keyHandler;
    double speed;
    double sprintSpeed;
    public Player(KeyHandler keyHandler, MouseHandler mouseHandler) {

        this.keyHandler = keyHandler;
        x = 0;
        y = 0;
        speed = 150;
        sprintSpeed = 250;
        hasCollision = true;
    }

    @Override
    public void updateOnFrame(double timePassedSecs) {
        double xChange = 0;
        double yChange = 0;
        // TODO: check the boundary of x and y
        if (keyHandler.shiftPressed) {
            if (keyHandler.wPressed && !keyHandler.sPressed) {
                yChange -= sprintSpeed * timePassedSecs;
            }
            if (keyHandler.sPressed && !keyHandler.wPressed) {
                yChange += sprintSpeed * timePassedSecs;
            }
            if (keyHandler.aPressed) {
                xChange -= sprintSpeed * timePassedSecs;
            }
            if (keyHandler.dPressed) {
                xChange += sprintSpeed * timePassedSecs;
            }
        } else {
            if (keyHandler.wPressed && !keyHandler.sPressed) {
                yChange -= speed * timePassedSecs;
            }
            if (keyHandler.sPressed && !keyHandler.wPressed) {
                yChange += speed * timePassedSecs;
            }
            if (keyHandler.aPressed) {
                xChange -= speed * timePassedSecs;
            }
            if (keyHandler.dPressed) {
                xChange += speed * timePassedSecs;
            }
        }
        double root2 = sqrt(2) / 2;
        if (xChange != 0 && yChange != 0) {
            x += xChange * root2;
            y += yChange * root2;
        } else {
            x += xChange;
            y += yChange;
        }
        if (x < 0) {
            x = 0;
        } else if (x > MAP_WIDTH) {
            x = MAP_WIDTH;
        }
        if (y < 0) {
            y = 0;
        } else if (y > MAP_HEIGHT) {
            y = MAP_HEIGHT;
        }
        Viewport.viewport.setX(x - (Viewport.viewport.getWidth() / 2));
        Viewport.viewport.setY(y - (Viewport.viewport.getHeight() / 2));
    }

    @Override
    public void draw(Graphics2D g2) {
        double scale = Viewport.viewport.getScale();
        g2.setColor(Color.white);
        g2.fillOval(
                (int)round(((x - Viewport.viewport.getX()) * scale) + Viewport.viewport.getXOffset()),
                (int)round(((y - Viewport.viewport.getY()) * scale) + Viewport.viewport.getYOffset()),
                (int)round(20 * scale),
                (int)round(20 * scale));
//        System.out.println(
//                (int)round((x - Viewport.viewport.getX()) * scale) + ":"
//                        + (int)round((y - Viewport.viewport.getY()) * scale));
    }
    public int[] getSector() {
        return new int[]{(int)floor(x / 1000), (int)floor(y / 1000)};
    }
}
