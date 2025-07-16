package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.*;


public class GamePanel extends JPanel implements Runnable {

//    double targetFPS = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate(); // 0 or negative number means unlimited
    double targetFPS = 0;

    double fps = 0;
    enum GameStatus {
        TITLE,
        SETTINGS,
        GAME_RUNNING,
        MAKING_NEW_GAME,
        LOADING_NEW_GAME
    }
    GameStatus gameStatus = GameStatus.TITLE;

    ArrayList<Long> frames = new ArrayList<>();

    KeyHandler keyHandler = new KeyHandler();
    MouseHandler mouseHandler = new MouseHandler();
    Thread gameThread;
    GameData gameData;

    public GamePanel() {
        this.setPreferredSize(new Dimension((int)floor((Viewport.viewport.getWidth() * Viewport.viewport.getScale()) + Viewport.viewport.getXOffset()*2), (int)floor((Viewport.viewport.getHeight() * Viewport.viewport.getScale()) + Viewport.viewport.getYOffset()*2)));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // buffer for better performance
        this.addKeyListener(keyHandler);
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
        fps = frames.size();
    }

    void newGame() {
         gameData = new GameData(123456, 10000000, 1000000, 20000, keyHandler, mouseHandler);
    }

    @Override
    public void run() {
        long currentTime;
        long lastTime = System.nanoTime();
        double targetFrameInterval = targetFPS > 0 ? 1000000000 / targetFPS : 0;

        while (gameThread != null) {
            if (targetFPS <= 0) {
                currentTime = System.nanoTime();
                double timePassedSec = (double) (currentTime - lastTime) / 1000000000;
                frames.add(currentTime);
                calculateFPS(currentTime);
                updateOnFrame(timePassedSec);
                repaint();
                lastTime = currentTime;
            } else {
                currentTime = System.nanoTime();
                if (currentTime - lastTime >= targetFrameInterval) {
                    double timePassedSec = (double) (currentTime - lastTime) / 1000000000;
                    frames.add(currentTime);
                    calculateFPS(currentTime);
                    updateOnFrame(timePassedSec);
                    repaint();
                    lastTime = currentTime;
                }
            }
        }
    }

    public void updateOnFrame(double timePassed) {
        if (gameStatus == GameStatus.GAME_RUNNING) {
            gameData.updateOnFrame(timePassed);
        } else if (gameStatus == GameStatus.TITLE) {

        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(120, 200, 10));
        g2.fillRect((int)Viewport.viewport.getXOffset(), (int)Viewport.viewport.getYOffset(), (int)ceil(Viewport.viewport.getWidth() * Viewport.viewport.getScale()), (int)ceil(Viewport.viewport.getHeight() * Viewport.viewport.getScale()));
        gameData.draw(g2);
        g2.setColor(Color.white);
        g2.drawString(String.valueOf(fps) + " target: " + targetFPS, 10, 10);
        g2.dispose();
    }
}
