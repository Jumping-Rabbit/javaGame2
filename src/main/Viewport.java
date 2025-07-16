package main;

import java.awt.*;

public class Viewport {
    double viewportX;
    double viewportY;
    double viewportWidth;
    double viewportHeight;
    double scale;
    double xOffset;
    double yOffset;
    public static final Viewport viewport = new Viewport();
    private Viewport() {
        // do scale stuff, and make an instance of this
        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double screenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
        double screenWidthScale = screenWidth / 1280;
        double screenHeightScale = screenHeight / 720;
        scale = Math.min(screenWidthScale, screenHeightScale);
        System.out.println(scale);
        viewportX = 0;
        viewportY = 0;
        viewportWidth = 1280;
        viewportHeight = 720;
        xOffset = ((screenWidth - (viewportWidth * scale)) / 2);
        yOffset = ((screenHeight - (viewportHeight * scale)) / 2);
        System.out.println(viewportWidth * scale);
        System.out.println(viewportHeight * scale);
        System.out.println(screenWidth);
        System.out.println(screenHeight);
        System.out.println(xOffset);
        System.out.println(yOffset);
    }

    public double getX() {
        return viewportX;
    }

    public void setX(double viewportX) {
        this.viewportX = viewportX;
    }

    public double getY() {
        return viewportY;
    }

    public void setY(double viewportY) {
        this.viewportY = viewportY;
    }

    public double getWidth() {
        return viewportWidth;
    }

    public void setWidth(int viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public double getHeight() {
        return viewportHeight;
    }

    public void setHeight(int viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public double getScale() {
        return scale;
    }

    public double getXOffset() {
        return xOffset;
    }

    public double getYOffset() {
        return scale;
    }
}
