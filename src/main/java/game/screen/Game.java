package game.screen;

import game.GameViewport;
import game.entity.Command;
import game.entity.Entity;
import game.entity.building.Building;
import utils.numUtil;
import game.entity.players;
import game.entity.unit.Unit;
import game.entity.unit.testRace1.Marine;
import utils.CollisionUtil;
import utils.DrawUtil;
import inputHandler.Input;
import inputHandler.InputHandler;
import inputHandler.InputType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tile.TileManager;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Game extends Screen {
    public enum GameState {
        RUNNING,
        PAUSED,
        MENU
    }
    File map;
    DrawUtil drawUtil;
    GameState gameState = GameState.RUNNING;
    GameViewport gameViewport;//get x and y from map
    TileManager tileManager;
    ArrayList<Unit> units;
    ArrayList<Building> buildings;
    ArrayList<Entity> selectedEntities;
    Rectangle2D.Double selectedRectangle = null;
    public Game(DrawUtil drawUtil, File map) {
        JSONParser parser = new JSONParser();
        Object object;
        try {
            object = parser.parse(new FileReader(map.getPath() + "/map.json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject mapJSON = (JSONObject) object;
        JSONArray playersData = (JSONArray) mapJSON.get("playerData");
        this(drawUtil, map, (int)Math.floor(Math.random() * playersData.size()));

    }
    public Game(DrawUtil drawUtil, File map, int playerNum) {
        units = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            units.add(new Marine(drawUtil, (int)(Math.random()*1000), (int)(Math.random()*700), players.BLUE));
        }
        buildings = new ArrayList<>();
        selectedEntities = new ArrayList<>();
        selectedEntities.addAll(units);

        tileManager = new TileManager(drawUtil, map);
        this.map = map;
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader(map.getPath() + "/map.json"));
            JSONObject mapJSON = (JSONObject) object;
            JSONArray playersData = (JSONArray) mapJSON.get("playerData");
            JSONObject playerData = (JSONObject) playersData.get(playerNum);
            gameViewport = new GameViewport(Double.parseDouble(String.valueOf(playerData.get("x"))), Double.parseDouble(String.valueOf(playerData.get("y"))));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        this.drawUtil = drawUtil;
        drawUtil.setGameViewport(gameViewport);
    }

    public Screen copy(){
        return new Game(this);
    }

    private Game(Game game){
        selectedRectangle = game.selectedRectangle;
        map = game.map;
        drawUtil = game.drawUtil;
        gameState = game.gameState;
        gameViewport = game.gameViewport;
        tileManager = game.tileManager;
        selectedEntities = new ArrayList<>();
        units = new ArrayList<>();
        for (Unit unit : game.units){
            Unit newUnit = unit.copy();
            units.add(newUnit);
            if (game.selectedEntities.contains(unit)){
                selectedEntities.add(newUnit);
            }
        }
        buildings = game.buildings;
    }

    private void calculatePhysics(){
        for (Unit unit1 : units){
            for (Unit unit2 : units){
                if (unit1 == unit2){
                    break;
                }
                if (CollisionUtil.CircleCircleCollision(unit1.getX(), unit1.getY(), unit1.getRadius(), unit2.getX(), unit2.getY(), unit2.getRadius())){
                    double distance = Math.sqrt(Math.pow(unit2.getX() - unit1.getX(), 2) + Math.pow(unit2.getY() - unit1.getY(), 2));
                    if (distance < unit1.getRadius() + unit2.getRadius() && distance > 0) {
                        double overlap = (unit1.getRadius() + unit2.getRadius()) - distance;
                        double ux = (unit2.getX() - unit1.getX()) / distance;
                        double uy = (unit2.getY() - unit1.getY()) / distance;
                        unit1.changeX(numUtil.DTL((ux * (overlap / 2))*-1));
                        unit1.changeY(numUtil.DTL((uy * (overlap / 2))*-1));
                        unit2.changeX(numUtil.DTL(ux * (overlap / 2)));
                        unit2.changeY(numUtil.DTL(uy * (overlap / 2)));
                    }

                }
            }
        }
    }

    public void updateOnFrame() {
        for (Input input : InputHandler.getInputs()){
            switch (input.getInputType()) {
                case LEFT_CLICK:
                    selectedEntities.clear();
                    for (Unit unit : units){
                        if(CollisionUtil.PointCircleCollision(input.getX(), input.getY(), numUtil.interpolate(unit.getLastX(), unit.getX(), drawUtil.getFactor())+unit.getRadius()-5, numUtil.interpolate(unit.getLastY(), unit.getY(), drawUtil.getFactor())+unit.getRadius()-5, unit.getRadius())){
                            selectedEntities.clear();
                            selectedEntities.add(unit);
                        }
                    }
                    break;
                case DRAG:
                    selectedEntities.clear();
                    for (Unit unit : units){
                        if(CollisionUtil.RectCircleCollision(numUtil.interpolate(unit.getLastX(), unit.getX(), drawUtil.getFactor())+unit.getRadius()-5, numUtil.interpolate(unit.getLastY(), unit.getY(), drawUtil.getFactor())+unit.getRadius()-5, unit.getRadius(), Math.min(input.getX(), input.getStartX()), Math.min(input.getY(), input.getStartY()), Math.abs(input.getX()-input.getStartX()), Math.abs(input.getX()-input.getStartX()))){
                            selectedEntities.add(unit);
                        }
                    }
                    selectedRectangle = new Rectangle2D.Double(Math.min(input.getX(), input.getStartX()), Math.min(input.getY(), input.getStartY()), Math.abs(input.getX()-input.getStartX()), Math.abs(input.getY()-input.getStartY()));
                    break;
                case RIGHT_CLICK:
                    for (Entity entity : selectedEntities){
                        entity.clearCommands();//make shift button work
                        entity.addCommand(new Command(InputType.RIGHT_CLICK, input.getX(), input.getY()));
                    }
                    break;
                case KEYPRESS:
                    if (Objects.equals(input.getKey(), "w")){
                        gameViewport.changeY(-10);
                    }else if (Objects.equals(input.getKey(), "a")){
                        gameViewport.changeX(-10);
                    } else if (Objects.equals(input.getKey(), "s")){
                        gameViewport.changeY(10);
                    }else if (Objects.equals(input.getKey(), "d")){
                        gameViewport.changeX(10);
                    }
                    break;
            }
        }
        for (Unit unit : units){
            unit.updateOnFrame();
        }
        for (Building building : buildings){
            building.updateOnFrame();
        }
        calculatePhysics();
        if (!InputHandler.MouseDown()){
            selectedRectangle = null;
        }
    }

    public void draw() {
        drawUtil.setGameViewport(gameViewport);
        for (Entity selected : selectedEntities){
            selected.drawSelectedRing();
        }
        for (Unit unit : units){
            unit.draw();
        }
        for (Building building : buildings){
            building.draw();
        }
        if (selectedRectangle != null){
            drawUtil.setColor(0, 255, 0, 50);
            drawUtil.fillRect(selectedRectangle);
        }

    }
}
