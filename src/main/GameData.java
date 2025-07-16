package main;

import entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.pow;

public class GameData {

    public static final int MAP_WIDTH = 200000;
    public static final int MAP_HEIGHT = 100000;

    public final Player player;

    public ArrayList<ArrayList<ArrayList<Entity>>> entitySectorMap = new ArrayList<>();

    public GameData(int seed, int grassAmount, int treeAmount, int rockAmount, KeyHandler keyHandler, MouseHandler mouseHandler) {
        Random random = new Random(seed);
        player = new Player(keyHandler, mouseHandler);
        for (int x = 0; x < 200; x++) {
            ArrayList<ArrayList<Entity>> entitySectorRow = new ArrayList<>();
            for (int y = 0; y < 100; y++) {
                ArrayList<Entity> entitySector = new ArrayList<>();
                for (int i = 0; i < grassAmount / 20000; i++) {
                    Grass grass = new Grass(random.nextInt(1000) + x*1000, random.nextInt(1000) + y*1000);
                    entitySector.add(grass);
                }
                for (int i = 0; i < treeAmount / 20000; i++) {
                    Tree tree = new Tree(random.nextInt(1000) + x*1000, random.nextInt(1000) + y*1000);
                    entitySector.add(tree);
                }
                for (int i = 0; i < rockAmount / 20000; i++) {
                    Rock rock = new Rock(random.nextInt(1000) + x*1000, random.nextInt(1000) + y*1000);
                    entitySector.add(rock);
                }
                entitySectorRow.add(entitySector);
            }
            entitySectorMap.add(entitySectorRow);
        }
//        for (int i = 0; i < grassAmount; i++) {
//            Grass grass = new Grass(random.nextInt(MAP_WIDTH), random.nextInt(MAP_HEIGHT));
//            entities.add(grass);
//        }
//        for (int i = 0; i < treeAmount; i++) {
//            Tree tree = new Tree(random.nextInt(MAP_WIDTH), random.nextInt(MAP_HEIGHT));
//            entities.add(tree);
//        }
//        for (int i = 0; i < rockAmount; i++) {
//            Rock rock = new Rock(random.nextInt(MAP_WIDTH), random.nextInt(MAP_HEIGHT));
//            entities.add(rock);
//        }
    }

    public void updateOnFrame(double timePassed) {
        player.updateOnFrame(timePassed);
        int[] playerSector = player.getSector();
        for (int x = playerSector[0] - 5; x <= playerSector[0] + 5; x++) {
            if (x < 0 || x > 200) {
                continue;
            }
            for (int y = playerSector[1] - 5; y <= playerSector[1] + 5; y++) {
                if ( y < 0 || y > 100){
                    continue;
                }
                for (Entity entity : entitySectorMap.get(x).get(y)) {
                    entity.updateOnFrame(timePassed);
                }
            }
        }
    }
    public void draw(Graphics2D g2) {
        //make only update the sectors around the player range 1
        int[] playerSector = player.getSector();
        for (int x = playerSector[0] - 1; x <= playerSector[0] + 1; x++) {
            if (x < 0 || x > 200) {
                continue;
            }
            for (int y = playerSector[1] - 1; y <= playerSector[1] + 1; y++) {
                if ( y < 0 || y > 100){
                    continue;
                }
                for (Entity entity : entitySectorMap.get(x).get(y)) {
                    entity.draw(g2);
                }
            }
        }
//        for (Entity entity : entities) {
//            if (pow((player.x - entity.x), 2) + pow((player.y - entity.y), 2) < 1000000) {
//                entity.draw(g2);
////                System.out.println(entity);
//            }
//        }
        player.draw(g2);
    }
}
