package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    int scale = 3;
    final int maxWidth = 1280;
    final int maxHeight = 720;
    int screenWidth = maxWidth * scale;
    int screenHeight = maxHeight * scale;

    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // buffer for better performance

    }
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run() {
        while(gameThread != null) {
            update();
            repaint();//call paintComponent
        }
    }
    public void update() {

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect(10, 20, 40, 40);
    }
}
