package com.company;



import com.company.graphic.MyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
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
        int numberOfPoints;
        try(Scanner scanner = new Scanner(new File("resources/berlin52.txt"))){
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

            myFrame = new MyFrame(x, y);

            Graph graph = new Graph(listOfPoints);
            for(Point p:graph.getPoints()){
                myFrame.addCircle(p.getX(), p.getY(), 5);
            }
            World world = new World(graph, 180, 23000, 0.88, 0.01);
            world.drawEveryXPoints(300);
            world.run();

            /*Path best = world.getBestPath();
            for(int i=0; i<best.getPath().size();i++){
                int temp=best.getPathAt(i);
                int tempNext;
                if(i==best.getPath().size()-1){
                    tempNext = best.getPathAt(0);
                }else{
                    tempNext = best.getPathAt(i+1);
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
