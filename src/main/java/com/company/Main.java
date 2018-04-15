package com.company;



import com.company.graphic.MyFrame;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;


public class Main{

    private final Logger log = Logger.getLogger(getClass().getName());

    public static MyFrame myFrame;
    public static final int GRAPH_SIZE_MULTIPLIER = 1;

    public static void main(String[] args) {
        List<Point> listOfPoints = new ArrayList<>();
        int maxFrameSize = 800;
        final int POPULATION_SIZE = 22;
        final long GENERATION_LIMIT = 0;
        final double CROSS_PROBAB = 0.92;
        final double MUTATION_PROBAB = 0.02;
        final String DATASET = "resources/bier127.tsp";
        final int DRAW_EVERY= 100;
        final int PRINT_EVERY= 1000;

        int numberOfPoints;
        try(Scanner scanner = new Scanner(new File(DATASET))){
            numberOfPoints = scanner.nextInt();
            int x=Integer.MIN_VALUE;
            int y = Integer.MIN_VALUE;
            for(int i=0; i<numberOfPoints; i++) {
                int sID = scanner.nextInt()-1;
                int sX = scanner.nextInt();
                int sY = scanner.nextInt();
                if(sX>x){
                    x = sX;
                }
                if(sY>y){
                    y = sY;
                }
                listOfPoints.add(new Point(sID, sX, sY));
            }

            myFrame = new MyFrame(x, y, maxFrameSize);

            Graph graph = new Graph(listOfPoints);
            World world = new World(graph, POPULATION_SIZE, GENERATION_LIMIT, CROSS_PROBAB, MUTATION_PROBAB);
            world.drawEveryXGenerations(DRAW_EVERY);
            world.printResultEveryXGenerations(PRINT_EVERY);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("The best finded path");
                try {
                    int[] best = world.getBestKnownPath().getPathAsArray();
                    for (int aBest : best) {
                        System.out.print(aBest + " ");
                    }
                    System.out.println();
                    System.out.println(world.getBestKnownPath().getPathLength());
                }catch (IllegalStateException e){
                    e.getMessage();
                    e.printStackTrace();
                }
            }, "Shutdown-thread"));
            world.run();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        }



    }

    public static void addLine(int x1, int y1, int x2, int y2, Color color){
        myFrame.addLine(x1,y1,x2,y2,color);
    }
    public static void clearFrame(){
        myFrame.removeAll();
        myFrame.revalidate();
        myFrame.repaint();
    }

    public static void clearLines(){
        myFrame.clearLines();
    }
}