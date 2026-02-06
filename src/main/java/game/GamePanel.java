package game;

import game.screen.*;
import inputHandler.InputHandler;
import utils.DrawUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;

import static java.lang.Math.*;


public class GamePanel extends JPanel{

//    double targetFPS = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate(); // 0 or negative number means unlimited
    double targetFPS = 0;
    enum GameStatus {
        TITLESCREEN,
        SETTINGS,
        GAME,
        MAP_EDITOR
    }
    private GameStatus gameStatus = GameStatus.TITLESCREEN;


    private Thread drawThread;
    private Thread logicThread;
    private Thread soundThread;

    private DrawUtil drawUtil;

    private Game game;
    private Screen tickScreen;

    private SettingsManager settingsManager;

    private TitleScreen titleScreen;
    private MapEditor mapEditor;
    private Settings settings;
    private PerformanceStorage performanceStorage;
    double lastTickTime = System.currentTimeMillis();

    private synchronized void setGameStatus(GameStatus gameStatus){
        this.gameStatus = gameStatus;
    }
    private synchronized GameStatus getGameStatus(){
        return gameStatus;
    }

    private synchronized void setGame(Game game){
        this.game = game;
    }
    private synchronized Game getGame(){
        return game;
    }

    private synchronized TitleScreen getTitleScreen() {
        return titleScreen;
    }
    private synchronized void setTitleScreen(TitleScreen titleScreen) {
        this.titleScreen = titleScreen;
    }

    private synchronized MapEditor getMapEditor() {
        return mapEditor;
    }
    private synchronized void setMapEditor(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    private synchronized Settings getSettings() {
        return settings;
    }
    private synchronized void setSettings(Settings settings) {
        this.settings = settings;
    }


    public GamePanel() {
        Dimension d = new Dimension((int)floor((Viewport.viewport.getWidth() * Viewport.viewport.getScale()) + Viewport.viewport.getXOffset()*2), (int)floor((Viewport.viewport.getHeight() * Viewport.viewport.getScale()) + Viewport.viewport.getYOffset()*2));
        this.setPreferredSize(d);
        this.setDoubleBuffered(true); // buffer for better performance

        this.addComponentListener(Viewport.viewport);
        this.addMouseListener(InputHandler.getMouseHandler());
        this.addMouseMotionListener(InputHandler.getMouseHandler());
        this.addMouseWheelListener(InputHandler.getMouseHandler());
        this.addKeyListener(InputHandler.getKeyHandler());
        this.setFocusable(true);
        settingsManager = new SettingsManager();
        settingsManager.getSettings();
        drawUtil = new DrawUtil();
        titleScreen = new TitleScreen(drawUtil);
        performanceStorage = new PerformanceStorage();
    }

    public void startGameThread() {
        drawThread = new Thread(new drawThread(performanceStorage));
        logicThread = new Thread(new logicThread(performanceStorage));
        drawThread.start();
        logicThread.start();
        SoundManager.startBGM();
    }

    private String formatString(Double num, String format){
        DecimalFormat df = new DecimalFormat(format);
        return Objects.toString(df.format(num));
    }

    public void updateOnFrame() {
        InputHandler.tick();
        this.requestFocusInWindow();
        switch (gameStatus){
            case GAME:
                tickScreen = getGame().copy();
                tickScreen.updateOnFrame();
                setGame((Game)tickScreen);
                break;
            case TITLESCREEN:
                tickScreen = getTitleScreen().copy();
                tickScreen.updateOnFrame();
                setTitleScreen((TitleScreen)tickScreen);
                if (getTitleScreen().isExit()){
                    switch (getTitleScreen().getSelectedButton()){
                        case MAP_EDITOR:
                            setMapEditor(new MapEditor(drawUtil));
                            setGameStatus(GameStatus.MAP_EDITOR);
                            break;
                        case SETTINGS:
                            setSettings(settings = new Settings(drawUtil));
                            setGameStatus(GameStatus.SETTINGS);
                            break;
                        case CUSTOM:
                            if (titleScreen.getSelectedFile() == null) break;
                            setGame(new Game(drawUtil, getTitleScreen().getSelectedFile()));
                            setGameStatus(GameStatus.GAME);
                            break;
                    }
                    getTitleScreen().resetSelections();
                }

                break;
            case MAP_EDITOR:
                tickScreen = getMapEditor().copy();
                tickScreen.updateOnFrame();
                setMapEditor((MapEditor)tickScreen);
                if (getMapEditor().isExit()){
                    setGameStatus(GameStatus.TITLESCREEN);
                }
                break;
            case SETTINGS:
                tickScreen = getSettings().copy();
                tickScreen.updateOnFrame();
                setSettings((Settings) tickScreen);
                if (getSettings().isExit()){
                    setGameStatus(GameStatus.TITLESCREEN);
                }
                break;
        }
        lastTickTime = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        switch (settingsManager.getGraphicsQuality()){
            case HIGH -> g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            case MEDIUM -> g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
            case LOW -> g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        }
        if (settingsManager.getAntialiasing()){
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else{
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        drawUtil.setFactor((System.currentTimeMillis()-lastTickTime)/50d);
        drawUtil.setG2(g2);
        drawUtil.fillBackground();
        switch (getGameStatus()){
            case GAME:
                getGame().draw();
                break;
            case TITLESCREEN:
                getTitleScreen().draw();
                break;
            case MAP_EDITOR:
                getMapEditor().draw();
                break;
            case SETTINGS:
                getSettings().draw();
                break;
        }

        g2.setColor(Color.white);
        g2.setFont(Fonts.fpsFont);
        g2.drawString("fps:" + formatString(performanceStorage.getFPS(), "00000.00") + " tps:" + formatString(performanceStorage.getTPS(), "00"), 10, 10);
        g2.drawString( "ttu:" + formatString(performanceStorage.getTickTimeUsed(), "0000.00") + "%" + " ttu1%:" + formatString(performanceStorage.getTickTimeUsedLow(), "0000.00") + "%" + " late frames:" + performanceStorage.getLateFrames(), 10, 20);

        g2.dispose();
    }
    class logicThread implements Runnable{
        PerformanceStorage performanceStorage;
        public logicThread(PerformanceStorage performanceStorage){
            this.performanceStorage = performanceStorage;
        }
        @Override
        public void run(){
            long currentTime;
            long targetFrameInterval = 50000000;//20 tps
            long targetTime = System.nanoTime() + targetFrameInterval;
            boolean waited = true;
            while (logicThread != null) {
                currentTime = System.nanoTime();
                if (currentTime >= targetTime) {
                    targetTime = currentTime + targetFrameInterval;
                    updateOnFrame();
                    if (!waited){
                        performanceStorage.addLateFrame();
                    }else{
                        waited = false;
                    }
                    performanceStorage.addTFrame(currentTime);
                    performanceStorage.addTickTimeUsed((double) (System.nanoTime()-currentTime)/500000d); //targetFrameInterval accounted for percentantage change
                    continue;
                }
                waited = true;
            }
        }
    }
    class drawThread implements Runnable{
        PerformanceStorage performanceStorage;
        public drawThread(PerformanceStorage performanceStorage){
            this.performanceStorage = performanceStorage;
        }
        private double targetFrameInterval;
        @Override
        public void run(){
            long currentTime;
            long lastTime = System.nanoTime();
            while (drawThread != null) {
                targetFPS = settingsManager.getTargetFPS();
                if (targetFPS == 0){
                    targetFrameInterval = 0;
                } else {
                    targetFrameInterval = 1000000000/targetFPS;
                }
                currentTime = System.nanoTime();
                if (currentTime - lastTime >= targetFrameInterval) {
                    lastTime = currentTime;
                    repaint();
                    performanceStorage.addDFrame(currentTime);
                }
            }
        }
    }

}
class PerformanceStorage{
    private LinkedList<Long> tFrames = new LinkedList<>();
    private LinkedList<Long> dFrames = new LinkedList<>();
    private LinkedList<Double> tickTimeUsed = new LinkedList<>();
    public int lateFrames = 0;
    public double fps;
    public double tps;
    public double ttu;
    private synchronized void setTps(double tps){
        this.tps = tps;
    }
    private synchronized void setFps(double fps){
        this.fps = fps;
    }
    private final ExecutorService executor = Executors.newFixedThreadPool(3);//amount of function that will need to be calculated

    public synchronized void addLateFrame(){
        lateFrames++;
    };
    private synchronized void addTickFrames(long currentFrame){
        tFrames.add(currentFrame);
    }
    public void addTFrame(long currentFrame){
        addTickFrames(currentFrame);
        executor.submit(this::calculateTPS);
        executor.submit(this::calculateFPS);
    }
    public synchronized void addDFrame(long currentFrame){
        dFrames.add(currentFrame);
    }
    private synchronized void addTTU(double ttu){
        tickTimeUsed.add(ttu);
    }
    public void addTickTimeUsed(double ttu){
        addTTU(ttu);
        executor.submit(this::calculateTTULow);
    }
    public synchronized double getTPS(){
        return tps;
    }
    public synchronized double getFPS(){
        return fps;
    }
    public synchronized int getLateFrames(){
        return lateFrames;
    }
    public synchronized double getTickTimeUsed(){
        if (tickTimeUsed.isEmpty()){
            return 0;
        }
        return tickTimeUsed.getLast();
    }
    public synchronized double getTickTimeUsedLow(){
        return ttu;
    }
    private synchronized LinkedList<Long> getTFrames(){
        return (LinkedList<Long>) tFrames.clone();
    }
    private synchronized void setTFrames (LinkedList<Long> tFrames){
        this.tFrames = tFrames;
    }
    private synchronized LinkedList<Long> getDFrames(){
        return (LinkedList<Long>) dFrames.clone();
    }
    private synchronized void setDFrames (LinkedList<Long> dFrames){
        this.dFrames = dFrames;
    }
    private void calculateTPS() {
        LinkedList<Long> newTFrames = getTFrames();
        newTFrames.removeIf(f -> System.nanoTime() - f >= 1000000000);
        setTFrames(newTFrames);
        setTps(tFrames.size());
    }

    private void calculateFPS() {
        LinkedList<Long> newDFrames = getDFrames();
        newDFrames.removeIf(f -> System.nanoTime() - f >= 2000000000);
        setDFrames(newDFrames);
        setFps(dFrames.size()/2f);
    }

    private synchronized void calculateTTULow(){
        if (tickTimeUsed.size()>100){
            tickTimeUsed.subList(0, tickTimeUsed.size()-100).clear();
        }
        double largest = 0;
        for (Double ttu : tickTimeUsed){
            if (ttu > largest){
                largest = ttu;
            }
        }
        ttu = largest;
    }
}
