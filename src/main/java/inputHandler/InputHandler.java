package inputHandler;

import game.Viewport;

import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Map;

public class InputHandler {
    private static final MouseHandler mouseHandler = new MouseHandler();
    private static final KeyHandler keyHandler = new KeyHandler();
    private static ArrayDeque<Input> inputs = new ArrayDeque<>();
    private static ArrayDeque<Input> inputsFinal = new ArrayDeque<>();


    public static KeyHandler getKeyHandler(){
        return keyHandler;
    }
    public static MouseHandler getMouseHandler(){
        return mouseHandler;
    }
    public static void tick(){
        inputsFinal = inputs;
        inputs = new ArrayDeque<>();
    };

    public static boolean MouseDown(){
        return mouseHandler.mouseDown();
    }

    protected static void addInput(Input input){
        inputs.addLast(input);
    }

    public static ArrayDeque<Input> getInputs(){
        return inputsFinal;
    }
}

class MouseHandler extends MouseAdapter {
    private double pressedX;
    private double pressedY;
    private static boolean isLeftDown = false;

    protected boolean mouseDown(){
        return isLeftDown;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1){
            InputHandler.addInput(new Input(InputType.LEFT_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        InputHandler.addInput(new Input(InputType.MOVE, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
    }

    @Override
    public void mousePressed(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1){
            isLeftDown = true;
        }

        pressedX = (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale();
        pressedY = (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale();
    }

    @Override
    public void mouseReleased(MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1) {
            isLeftDown = false;
        }
        if (e.getButton() == MouseEvent.BUTTON2){
            InputHandler.addInput(new Input(InputType.MIDDLE_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        } else if (e.getButton() == MouseEvent.BUTTON3){
            InputHandler.addInput(new Input(InputType.RIGHT_CLICK, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        }
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        if (isLeftDown){
            InputHandler.addInput(new Input(InputType.DRAG, pressedX, pressedY, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale()));
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        InputHandler.addInput(new Input(InputType.SCROLL, (e.getX() - Viewport.viewport.getXOffset())/Viewport.viewport.getScale(), (e.getY() - Viewport.viewport.getYOffset())/Viewport.viewport.getScale(), e.getWheelRotation()));
    }


}


class KeyHandler implements KeyListener {
    String[] keys = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d",
            "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "shift", "control", "enter", "backspace", "space", "up", "right", "down", "left", "escape"};

    @Override
    public void keyTyped(KeyEvent e) {}

    private static final Map<Integer, String> SPECIAL_KEYS = Map.ofEntries(
            Map.entry(KeyEvent.VK_SHIFT, "shift"),
            Map.entry(KeyEvent.VK_CONTROL, "control"),
            Map.entry(KeyEvent.VK_ENTER, "enter"),
            Map.entry(KeyEvent.VK_SPACE, "space"),
            Map.entry(KeyEvent.VK_BACK_SPACE, "backspace"),
            Map.entry(KeyEvent.VK_UP, "up"),
            Map.entry(KeyEvent.VK_DOWN, "down"),
            Map.entry(KeyEvent.VK_LEFT, "left"),
            Map.entry(KeyEvent.VK_RIGHT, "right"),
            Map.entry(KeyEvent.VK_ESCAPE, "escape")
    );
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        String keyName = SPECIAL_KEYS.get(code);

        if (keyName == null) {
            if (KeyEvent.getKeyText(code).length() == 1) {
                keyName = KeyEvent.getKeyText(code).toLowerCase();
            }
        }

        if (keyName != null) {
            InputHandler.addInput(new Input(InputType.KEYPRESS, keyName));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
    }
}

