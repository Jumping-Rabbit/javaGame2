package main;

import entity.Grass;
import entity.Rock;
import entity.Tree;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    public static class Map{
        int mapWidth = 1280000;
        int mapHeight = 720000;
        public static class Objects{
            public static ArrayList<Grass> grasses = new ArrayList<>();
            public static ArrayList<Tree> trees = new ArrayList<>();
            public static ArrayList<Rock> rocks = new ArrayList<>();
            GamePanel gp = new GamePanel();
            Random random = new Random(gp.seed);
            public Objects() {
                generateObjects(1000, 100, 10);
            }

            public void generateObjects(int grassAmount, int treeAmount, int rockAmount) {

                for (int i = 0; i < grassAmount; i++) {
                    Grass grass = new Grass(random.nextInt(gp.screenWidth), random.nextInt(gp.screenHeight));
                    grasses.add(grass);
                }
                for (int i = 0; i < treeAmount; i++) {
                    Tree tree = new Tree(random.nextInt(gp.screenWidth), random.nextInt(gp.screenHeight));
                    trees.add(tree);
                }
                for (int i = 0; i < rockAmount; i++) {
                    Rock rock = new Rock(random.nextInt(gp.screenWidth), random.nextInt(gp.screenHeight));
                    rocks.add(rock);
                }
            }
        }
    }
}
