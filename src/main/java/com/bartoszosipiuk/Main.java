package com.bartoszosipiuk;



import com.bartoszosipiuk.GUI.MyFrame;
import com.bartoszosipiuk.models.Graph;
import com.bartoszosipiuk.models.NoPathGeneratedException;
import com.bartoszosipiuk.models.Point;
import com.bartoszosipiuk.models.World;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class Main{

    protected static final Logger log = Logger.getLogger(Main.class);

    private static MyFrame myFrame;
    public static final int GRAPH_SIZE_MULTIPLIER = 1;

    public static void main(String[] args) {
        List<com.bartoszosipiuk.models.Point> listOfPoints = new ArrayList<>();

        int maxFrameSize;
        final int POPULATION_SIZE;
        final long GENERATION_LIMIT;
        final double CROSS_PROBAB;
        final double MUTATION_PROBAB;
        final String DATASET;
        final int DRAW_EVERY;
        final int PRINT_EVERY;

        try(InputStream input = Main.class.getResourceAsStream("/genetic.properties")){
            Properties prop = new Properties();
            prop.load(input);
            maxFrameSize = Integer.parseInt(prop.getProperty("max_frame_size"));
            POPULATION_SIZE = Integer.parseInt(prop.getProperty("population_size"));
            GENERATION_LIMIT = Integer.parseInt(prop.getProperty("generation_limit"));
            CROSS_PROBAB = Double.parseDouble(prop.getProperty("cross_probab"));
            MUTATION_PROBAB = Double.parseDouble(prop.getProperty("mutation_probab"));
            DATASET = prop.getProperty("dataset_path");
            DRAW_EVERY = Integer.parseInt(prop.getProperty("draw_every"));
            PRINT_EVERY = Integer.parseInt(prop.getProperty("print_every"));

            log.debug("All properties loaded");
        } catch (IOException e) {
            log.fatal(e);
            return;
        }


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
                StringBuilder sb = new StringBuilder();
                try {
                    int[] best = world.getBestKnownPath().getPathAsArray();
                    for (int aBest : best) {
                        sb.append(aBest).append(" ");
                    }
                    sb.append("\n");
                    log.trace("The best found path: \n"+sb.toString());
                    log.debug("The best path: " + world.getBestKnownPath().getPathLength());
                }catch (NoPathGeneratedException e){
                    log.warn(e);
                }
            }, "Shutdown-thread"));
            world.run();
        } catch (FileNotFoundException e) {
            log.fatal("Dataset not found! " + e);
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