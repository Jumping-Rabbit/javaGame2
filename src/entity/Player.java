package entity;

import main.KeyHandler;
import main.GamePanel;

import java.awt.*;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    double speed;
    double sprintSpeed;
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefualtValues();
    }
    public void setDefualtValues() {
        x = 100;
        y = 100;
        speed = 100;
        sprintSpeed = 180;
        hasCollision = true;
    }
    public void update() {
        if (keyH.shiftPressed) {
            if (keyH.wPressed) {
                y -= sprintSpeed * gp.refreshTime;
            }
            if (keyH.sPressed) {
                y += sprintSpeed * gp.refreshTime;
            }
            if (keyH.aPressed) {
                x -= sprintSpeed * gp.refreshTime;
            }
            if (keyH.dPressed) {
                x += sprintSpeed * gp.refreshTime;
            }
        } else {
            if (keyH.wPressed) {
                y -= speed * gp.refreshTime;
            }
            if (keyH.sPressed) {
                y += speed * gp.refreshTime;
            }
            if (keyH.aPressed) {
                x -= speed * gp.refreshTime;
            }
            if (keyH.dPressed) {
                x += speed * gp.refreshTime;
            }
        }
    }
    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect((int)Math.round(x), (int)Math.round(y), 40, 40);
    }
}
