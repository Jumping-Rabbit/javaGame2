package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable{
    int scale = 3;
    final int maxWidth = 1280;
    final int maxHeight = 720;
    int screenWidth = maxWidth * scale;
    int screenHeight = maxHeight * scale;

    String targetFPS = "unlimited";
    float FPS = 0;

    long currentTime;
    double delta = 0;
    long lastTime = System.nanoTime();
    double refreshTime = 0;
    double FPSInterval;
    ArrayList<Long> frames = new ArrayList<>();

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

        double playerX = 100;
        double playerY = 100;
        double playerSpeed = 100;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // buffer for better performance
        this.addKeyListener(keyH);
        this.setFocusable(true);

    }
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void calculateFPS() {
        for (int i = frames.size() - 1; i >= 0; i--) {
            if (currentTime - frames.get(i) >= 1000000000) {
                frames.remove(i);
            }
        }
        FPS = frames.size();
    }
    @Override
    public void run() {
        while(gameThread != null) {
            if (Objects.equals(targetFPS, "unlimited")) {
                currentTime = System.nanoTime();
                refreshTime = (double)(currentTime - lastTime) / 1000000000;
                lastTime = currentTime;
                frames.add(currentTime);
                calculateFPS();
                update();
                repaint();
            } else {
                FPSInterval = 1000000000 / Double.parseDouble(targetFPS);
                currentTime = System.nanoTime();

                delta += (currentTime - lastTime) / FPSInterval;
                lastTime = currentTime;
                if (delta >= 1) {
                    frames.add(currentTime);
                    calculateFPS();
                    update();
                    repaint();
                    delta--;
                }

            }

        }
    }
    public void update() {
        if (keyH.wPressed) {
            playerY -= playerSpeed * refreshTime;
        }
        if (keyH.sPressed) {
            playerY += playerSpeed * refreshTime;
        }
        if (keyH.aPressed) {
            playerX -= playerSpeed * refreshTime;
        }
        if (keyH.dPressed) {
            playerX += playerSpeed * refreshTime;
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect((int)Math.round(playerX), (int)Math.round(playerY), 40, 40);
        g2.drawString(String.valueOf(FPS), 10, 10);
        g2.dispose();
    }
}
