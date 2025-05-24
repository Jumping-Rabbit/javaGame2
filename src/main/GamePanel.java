package main;

import entity.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static main.Game.Map.Objects.*;

public class GamePanel extends JPanel implements Runnable{
    int scale = 3;
    final int cameraMaxWidth = 1280;
    final int cameraMaxHeight = 720;
    public int screenWidth = cameraMaxWidth * scale;
    public int screenHeight = cameraMaxHeight * scale;
    public int seed = 192837465;

    String targetFPS = "unlimited";
    float FPS = 0;

    public double refreshTime = 0;
    double FPSInterval;
    ArrayList<Long> frames = new ArrayList<>();

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this,keyH);
    Game game = new Game();
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

    public void calculateFPS(long currentTime) {
        for (int i = frames.size() - 1; i >= 0; i--) {
            if (currentTime - frames.get(i) >= 1000000000) {
                frames.remove(i);
            }
        }
        FPS = frames.size();
    }

    @Override
    public void run() {
        System.out.println(grasses.size());
        long currentTime;
        long lastTime = System.nanoTime();
        long lastDrawnTime = System.nanoTime();
        double delta = 0;
        while(gameThread != null) {
            if (Objects.equals(targetFPS, "unlimited")) {
                currentTime = System.nanoTime();
                refreshTime = (double)(currentTime - lastTime) / 1000000000;
                frames.add(currentTime);
                calculateFPS(currentTime);
                update();
                repaint();
                lastTime = currentTime;
            } else {
                FPSInterval = 1000000000 / Double.parseDouble(targetFPS);
                currentTime = System.nanoTime();
                delta += (currentTime - lastTime) / FPSInterval;
                if (delta >= 1) {
                    refreshTime = (double)(currentTime - lastDrawnTime) / 1000000000;
                    frames.add(currentTime);
                    calculateFPS(currentTime);
                    update();
                    repaint();
                    delta--;
                    lastDrawnTime = currentTime;
                }
                lastTime = currentTime;
            }

        }
    }
    public void update() {
        player.update();
        // do collision here
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(120,200,10));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        for (Grass grass : grasses) {
            grass.draw(g2);
        }
        player.draw(g2);
        for (Tree tree : trees) {
            tree.draw(g2);
        }
        for (Rock rock : rocks) {
            rock.draw(g2);
        }
        g2.setColor(Color.white);
        g2.drawString(String.valueOf(FPS), 10, 10);
        g2.dispose();
    }
}
