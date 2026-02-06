package game.screen;

import utils.CollisionUtil;
import utils.DrawUtil;
import inputHandler.Input;
import inputHandler.InputHandler;

import java.awt.*;
import java.util.Objects;

public class Settings extends Screen{
    public enum Buttons {
        GRAPHICS(new Rectangle(0, 0, 860, 100), "graphics"),
        AUDIO(new Rectangle(860, 0, 860, 100), "audio"),
        EXIT(new Rectangle(1720, 0, 200, 100), "exit");

        private final Rectangle  rectangle;
        private final String name;
        Buttons(Rectangle rectangle, String name){
            this.rectangle = rectangle;
            this.name = name;
        }
        private String getName(){
            return name;
        }
        private Rectangle getRectangle(){
            return rectangle;
        }
    }
    public enum AudioButtons {
        MASTER_VOLUME(new Rectangle(50, 150, 1000, 100), "master volume"),
        BGM_VOLUME(new Rectangle(50, 300, 1000, 100), "bgm volume");

        private final Rectangle rectangle;
        private final String name;
        AudioButtons(Rectangle rectangle, String name){
            this.rectangle = rectangle;
            this.name = name;
        }
        private String getName(){
            return name;
        }
        private Rectangle getRectangle(){
            return rectangle;
        }
    }
    public enum GraphicsButtons {
        GRAPHICS_LEVEL(new Rectangle(50, 150, 1000, 100), "graphics level"),
        MONITOR_NUM(new Rectangle(50, 300, 1000, 100), "monitor num"),
        TARGET_FPS(new Rectangle(50, 450, 1000, 100), "target fps"),
        ANTIALIASING(new Rectangle(50, 600, 1000, 100), "antialiasing"),
        DISPLAY_MODE(new Rectangle(50, 750, 1000, 100), "display mode");

        private final Rectangle rectangle;
        private final String name;
        GraphicsButtons(Rectangle rectangle, String name){
            this.rectangle = rectangle;
            this.name = name;
        }
        private String getName(){
            return name;
        }
        private Rectangle getRectangle(){
            return rectangle;
        }
    }
    private DrawUtil drawUtil;
    private boolean isEditing = false;
    private Buttons currentSection = Buttons.GRAPHICS;
    private GraphicsButtons currentGraphicsSetting = GraphicsButtons.GRAPHICS_LEVEL;
    private AudioButtons currentAudioSetting = AudioButtons.MASTER_VOLUME;
    private boolean exit = false;

    public boolean isExit() {
        if (exit){
            exit = false;
            return true;
        }
        return false;
    }

    public void resetSelections(){
        isEditing = false;
        currentSection = Buttons.GRAPHICS;
        currentGraphicsSetting = GraphicsButtons.GRAPHICS_LEVEL;
        currentAudioSetting = AudioButtons.MASTER_VOLUME;
    }

    public Settings(DrawUtil drawUtil) {
        this.drawUtil = drawUtil;
    }

    public Settings(Settings settings){
        drawUtil = settings.drawUtil;
        isEditing = settings.isEditing;
        currentSection = settings.currentSection;
        currentGraphicsSetting = settings.currentGraphicsSetting;
        currentAudioSetting = settings.currentAudioSetting;
        exit = settings.exit ;
    }

    public Screen copy(){
        return new Settings(this);
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case KEYPRESS:
                    if (Objects.equals(input.getKey(), "a") || Objects.equals(input.getKey(), "left")){
                        if (!isEditing) {
                            Buttons oldButton = currentSection;
                            currentSection = Buttons.values()[(currentSection.ordinal() - 1) >= 0 ? (currentSection.ordinal() - 1) : (Buttons.values().length - 1)];
                            if (currentSection != oldButton) {
                                isEditing = false;
                                currentGraphicsSetting = GraphicsButtons.GRAPHICS_LEVEL;
                                currentAudioSetting = AudioButtons.MASTER_VOLUME;
                            }
                            break;
                        }
                    } else if (Objects.equals(input.getKey(), "d") || Objects.equals(input.getKey(), "right")){
                        if(!isEditing) {
                            Buttons oldButton = currentSection;
                            currentSection = Buttons.values()[(currentSection.ordinal() + 1) % Buttons.values().length];
                            if (currentSection != oldButton) {
                                isEditing = false;
                                currentGraphicsSetting = GraphicsButtons.GRAPHICS_LEVEL;
                                currentAudioSetting = AudioButtons.MASTER_VOLUME;
                            }
                        }
                    } else if (Objects.equals(input.getKey(), "w")) {
                        if(!isEditing) {
                            switch (currentSection) {
                                case GRAPHICS:
                                    currentGraphicsSetting = GraphicsButtons.values()[(currentGraphicsSetting.ordinal() - 1) >= 0 ? (currentGraphicsSetting.ordinal() - 1) : (GraphicsButtons.values().length - 1)];
                                    break;
                                case AUDIO:
                                    currentAudioSetting = AudioButtons.values()[(currentAudioSetting.ordinal() - 1) >= 0 ? (currentAudioSetting.ordinal() - 1) : (AudioButtons.values().length - 1)];
                                    break;
                            }
                        }
                    } else if (Objects.equals(input.getKey(), "s")) {
                        if(!isEditing) {
                            switch (currentSection) {
                                case GRAPHICS:
                                    currentGraphicsSetting = GraphicsButtons.values()[(currentGraphicsSetting.ordinal() + 1) % GraphicsButtons.values().length];
                                    break;
                                case AUDIO:
                                    currentAudioSetting = AudioButtons.values()[(currentAudioSetting.ordinal() + 1) % AudioButtons.values().length];
                                    break;
                            }
                        }
                    } else if (Objects.equals(input.getKey(), "escape")){
                        if (isEditing){
                            isEditing = false;
                            break;
                        }
                        exit = true;
                    } else if (Objects.equals(input.getKey(), "enter")){
                        isEditing = !isEditing;
                    }
                    break;
                case LEFT_CLICK:
//                    for (int i = 0; i < sections.length; i++) {
//                        if (CollisionUtil.RectPointCollision(sectionWidth*i, 0, sectionWidth, 100, input.getX(), input.getY())) {
//                            int oldIndex = sectionIndex;
//                            sectionIndex = i;
//                            if (sectionIndex != oldIndex){
//                                settingsIndex = 0;
//                            }
//                        }
//                    }
                    break;
                case SCROLL:
//                    settingsIndex = Math.min(Math.max(sectionIndex+input.getScroll(), 0), sectionSettings.size()-2);
//                    break;
            }
//            System.out.println(selectedIndex);
//            if (sectionIndex == sections.length-1){
//                exit = true;
//            }
        }

    }

    public void draw() {
        drawUtil.setColor(75, 75, 75);
        drawUtil.fillRect(0, 0, 1920, 100);
        drawUtil.setThickness(5);
        for (Buttons button : Buttons.values()){
            if (button == currentSection){
                drawUtil.setColor(0, 255, 255);
            } else{
                drawUtil.setColor(0, 150, 255);
            }
            drawUtil.drawRect(button.getRectangle());
            drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth()/2, 50, button.getName(), 20);
        }
        switch(currentSection){
            case GRAPHICS:
                for (GraphicsButtons button : GraphicsButtons.values()){
                    if (button == currentGraphicsSetting){
                        drawUtil.setColor(0, 255, 255);
                    } else{
                        drawUtil.setColor(0, 150, 255);
                    }
                    drawUtil.drawRect(button.getRectangle());
                    drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth()/3, button.getRectangle().getY() + button.getRectangle().getHeight()/2, button.getName(), 20);
                }
                break;
            case AUDIO:
                for (AudioButtons button : AudioButtons.values()){
                    if (button == currentAudioSetting){
                        drawUtil.setColor(0, 255, 255);
                    } else{
                        drawUtil.setColor(0, 150, 255);
                    }
                    drawUtil.drawRect(button.getRectangle());
                    drawUtil.drawString(button.getRectangle().getX() + button.getRectangle().getWidth()/3, button.getRectangle().getY() + button.getRectangle().getHeight()/2, button.getName(), 20);
                }
                break;
        }
    }
}