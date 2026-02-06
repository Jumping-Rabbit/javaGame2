package game;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsManager{
    public enum Settings{
        TARGET_FPS,
        MONITOR_NUM,
        DISPLAY_MODES,
        MASTER_VOLUME,
        BGM_VOLUME,
        ANTIALIASING,
        GRAPHICS_QUALITY
    }

    private final double minTargetFPS = 0.1;
    private double targetFPS = 0.1;
    private final Object targetFPSLock = new Object();


    private final int minMonitorNum = 0;
    private int monitorNum = 0;
    private final Object monitorNumLock = new Object();


    public enum DisplayModes{
        WINDOWED("windowed"),
        WINDOWED_FULLSCREEN("windowedFullscreen"),
        FULLSCREEN("fullscreen");

        private final String string;
        DisplayModes(String string){
            this.string = string;
        }
        private String getString(){
            return string;
        }
        private static DisplayModes fromValue(String givenName) {
            for (DisplayModes displayMode : values()) {
                if (displayMode.string.equalsIgnoreCase(givenName)) {
                    return displayMode;
                }
            }
            return null;
        }
    }
    private DisplayModes displayMode = DisplayModes.WINDOWED;
    private final Object displayModeLock = new Object();


    private final double minMasterVolume = 0;
    private final double maxMasterVolume = 100;
    private double masterVolume = 0;
    private final Object masterVolumeLock = new Object();

    private final double minBGMVolume = 0;
    private final double maxBGMVolume = 100;
    private double BGMVolume = 0;
    private final Object BGMVolumeLock = new Object();

    private boolean antialiasing = true;
    private final Object antialiasingLock = new Object();

    public enum GraphicsQuality{
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high");

        private final String string;
        GraphicsQuality(String string){
            this.string = string;
        }
        private String getString(){
            return string;
        }
        private static GraphicsQuality fromValue(String givenName) {
            for (GraphicsQuality graphicsQuality : values()) {
                if (graphicsQuality.string.equalsIgnoreCase(givenName)) {
                    return graphicsQuality;
                }
            }
            return null;
        }
    }
    private GraphicsQuality graphicsQuality = GraphicsQuality.HIGH;
    private final Object graphicsQualityLock = new Object();




    private void writeSettings(String directory, String key, Object value){
//        return;
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("src/main/res/settings.json"));
            JSONObject settings = (JSONObject) object;
            JSONObject selectedDirectory = (JSONObject) settings.get(directory);
            selectedDirectory.put(key, value);
            try (FileWriter file = new FileWriter("src/main/res/settings.json")) {
                file.write(settings.toJSONString());
                file.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void getSettings(){
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("src/main/res/settings.json"));
            JSONObject settings = (JSONObject) object;
            JSONObject graphics = (JSONObject) settings.get("graphics");
            JSONObject audio = (JSONObject) settings.get("audio");
            setMonitorNum((int)(long)graphics.get("monitorNum"));
            setDisplayMode(DisplayModes.fromValue(String.valueOf(graphics.get("displayMode"))));
            setGraphicsQuality(GraphicsQuality.fromValue(String.valueOf(graphics.get("graphicsQuality"))));
            setAntialiasing(Boolean.getBoolean(String.valueOf(graphics.get("antialiasing"))));
            try{setTargetFPS((double)graphics.get("targetFPS"));} catch (RuntimeException e) {setTargetFPS((long)graphics.get("targetFPS"));}
            try{SoundManager.setMasterVolume((double)audio.get("masterVolume"));} catch (RuntimeException e) {SoundManager.setMasterVolume((long)audio.get("masterVolume"));}
            try{SoundManager.setBGMVolume((double)audio.get("BGMVolume"));} catch (RuntimeException e) {SoundManager.setBGMVolume((long)audio.get("BGMVolume"));}
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public void setTargetFPS(double targetFPS){
        targetFPS = Math.max(targetFPS, minTargetFPS);
        if (targetFPS < minTargetFPS){
            return;
        }
        synchronized (targetFPSLock){
            this.targetFPS = targetFPS;
            writeSettings("graphics", "targetFPS", targetFPS);
        }
    }
    public double getTargetFPS(){
        synchronized (targetFPSLock) {
            return targetFPS;
        }
    }

    public void setMonitorNum(int monitorNum){
        monitorNum = Math.max(monitorNum, minMonitorNum);
        synchronized (monitorNumLock){
            this.monitorNum = monitorNum;
            writeSettings("graphics", "monitorNum", monitorNum);
            synchronized (displayModeLock) {
                switch (displayMode) {
                    case WINDOWED:
                        Main.setWindowed();
                        break;
                    case WINDOWED_FULLSCREEN:
                        Main.setWindowedBorderless(this.monitorNum);
                        break;
                    case FULLSCREEN:
                        Main.setFullscreen(monitorNum);
                        break;
                }
            }
        }
    }
    public int getMonitorNum(){
        synchronized (monitorNumLock) {
            return monitorNum;
        }
    }

    public void setDisplayMode(DisplayModes displayMode){
        synchronized (displayModeLock){
            if(this.displayMode == displayMode) {
                return;
            }
            this.displayMode = displayMode;
            writeSettings("graphics", "displayMode", displayMode.string);
            switch (displayMode){
                case WINDOWED:
                    Main.setWindowed();
                    break;
                case WINDOWED_FULLSCREEN:
                    synchronized (monitorNumLock){
                        Main.setWindowedBorderless(this.monitorNum);
                    }
                    break;
                case FULLSCREEN:
                    synchronized (monitorNumLock){
                        Main.setFullscreen(monitorNum);
                    }
                    break;
            }
        }
    }
    public DisplayModes getDisplayMode(){
        synchronized (displayModeLock) {
            return displayMode;
        }
    }

    public void setMasterVolume(double volume){
        volume = Math.clamp(volume, minMasterVolume, maxMasterVolume);
        synchronized (masterVolumeLock){
            masterVolume = volume;
            writeSettings("audio", "masterVolume", masterVolume);
        }
    }
    public double getMasterVolume(){
        synchronized (masterVolumeLock) {
            return masterVolume;
        }
    }

    public void setBGMVolume(double volume){
        volume = Math.clamp(volume, minBGMVolume, maxBGMVolume);
        synchronized (BGMVolumeLock){
            BGMVolume = volume;
            writeSettings("audio", "BGMVolume", BGMVolume);
        }
    }
    public double getBGMVolume(){
        synchronized (BGMVolumeLock) {
            return BGMVolume;
        }
    }


    public boolean getAntialiasing() {
        synchronized (antialiasingLock){
            return antialiasing;
        }
    }
    public void setAntialiasing(boolean antialiasing) {
        synchronized (antialiasingLock){
            this.antialiasing = antialiasing;
            writeSettings("graphics", "antialiasing", antialiasing);
        }
    }

    public void setGraphicsQuality(GraphicsQuality graphicsQuality){
        synchronized (graphicsQualityLock){
            this.graphicsQuality = graphicsQuality;
            writeSettings("graphics", "graphicsQuality", graphicsQuality.string);
        }
    }
    public GraphicsQuality getGraphicsQuality(){
        synchronized (graphicsQualityLock) {
            return graphicsQuality;
        }
    }

}
