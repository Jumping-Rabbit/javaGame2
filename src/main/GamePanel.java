package main;

import entity.Grass;
import entity.Player;
import entity.Rock;
import entity.Tree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable{
    int scale = 3;
    final int maxWidth = 1280;
    final int maxHeight = 720;
    int screenWidth = maxWidth * scale;
    int screenHeight = maxHeight * scale;
    long seed = 192837465;
    Random random = new Random(seed);

    String targetFPS = "unlimited";
    float FPS = 0;


    public double refreshTime = 0;
    double FPSInterval;
    ArrayList<Long> frames = new ArrayList<>();
    ArrayList<Grass> grasses = new ArrayList<>();
    ArrayList<Tree> trees = new ArrayList<>();
    ArrayList<Rock> rocks = new ArrayList<>();
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this,keyH);

    public void generateObjects(int grassAmount, int treeAmount, int rockAmount) {
        for (int i = 0; i < grassAmount; i++) {
            Grass grass = new Grass(random.nextInt(screenWidth), random.nextInt(screenHeight));
            grasses.add(grass);
        }
        for (int i = 0; i < treeAmount; i++) {
            Tree tree = new Tree(random.nextInt(screenWidth), random.nextInt(screenHeight));
            trees.add(tree);
        }
        for (int i = 0; i < rockAmount; i++) {
            Rock rock = new Rock(random.nextInt(screenWidth), random.nextInt(screenHeight));
            rocks.add(rock);
        }
    }
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // buffer for better performance
        this.addKeyListener(keyH);
        this.setFocusable(true);
        generateObjects(1000, 100, 10);

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
