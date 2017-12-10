package com.company;



import com.company.graphic.MyFrame;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main{

    public static MyFrame myFrame;
    public static final int GRAPH_SIZE_MULTIPLIER = 1;
    public static void main(String[] args) {
        List<Point> listOfPoints = new ArrayList<>();
        int maxFrameSize = 1000;
        final int POPULATION_SIZE = 20;
        final long GENERATION_LIMIT = 0;
        final double CROSS_PROBAB = 0.90;
        final double MUTATION_PROBAB = 0.01;
        final String DATASET = "resources/a280.tsp";
        final int DRAW_EVERY= 1000;
        final int PRINT_EVERY= 500;

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
            for(Point p:graph.getPoints()){
                myFrame.addCircle(p.getX(), p.getY(), 5);
            }
            World world = new World(graph, POPULATION_SIZE, GENERATION_LIMIT, CROSS_PROBAB, MUTATION_PROBAB);
            world.drawEveryXGenerations(DRAW_EVERY);
            world.printResultEveryXGenerations(PRINT_EVERY);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    System.out.println("The best finded path");
                    try {
                        int[] best = world.getBestKnownPath().getPathAsArray();
                        for (int aBest : best) {
                            System.out.print(aBest + " ");
                        }
                        System.out.println();
                    }catch (IllegalStateException e){
                        e.getMessage();
                        e.printStackTrace();
                    }
                }
            }, "Shutdown-thread"));
            world.run();

            /*Path best = world.getBestPath();
            for(int i=0; i<best.getPath().size();i++){
                int temp=best.getIdAt(i);
                int tempNext;
                if(i==best.getPath().size()-1){
                    tempNext = best.getIdAt(0);
                }else{
                    tempNext = best.getIdAt(i+1);
                }
                Point p = graph.getPointAt(temp);
                Point pNext = graph.getPointAt(tempNext);
                myFrame.addLine(p.getX(), p.getY(), pNext.getX(), pNext.getY(), Color.BLACK);
            }*/
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
