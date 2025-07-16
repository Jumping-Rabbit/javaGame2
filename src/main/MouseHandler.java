package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseMotionListener{
    public int mouseX;
    public int mouseY;
    public int mouseClickedX;
    public int mouseClickedY;
    public boolean mouseDown = false;

    public void mouseClicked(MouseEvent e) {
        mouseClickedX = e.getXOnScreen();
        mouseClickedY = e.getYOnScreen();
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getXOnScreen();
        mouseY = e.getYOnScreen();
    }
}
