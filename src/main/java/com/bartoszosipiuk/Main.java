package com.bartoszosipiuk;


import com.bartoszosipiuk.gui.MyFrame;
import com.bartoszosipiuk.model.*;
import com.bartoszosipiuk.model.Point;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    private static MyFrame myFrame;
    public static final int GRAPH_SIZE_MULTIPLIER = 1;

    private static int maxFrameSize;
    private static int populationSi;
    private static long generationLimit;
    private static double crossProbab;
    private static double mutationProbab;
    private static String dataset;
    private static int drawEvery;
    private static int printEvery;
    private static boolean printBestPathOnShutdown;

    private static Graph graph;
    private static World world;

    public static void main(String[] args) {
        try {
            readProperties();
        } catch (IOException e) {
            return;
        }

        try (Scanner scanner = new Scanner(new File(dataset))) {
            List<Point> listOfPoints = new ArrayList<>();
            int numberOfPoints = scanner.nextInt();
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (int i = 0; i < numberOfPoints; i++) {
                int pointID = scanner.nextInt() - 1;
                int pointX = scanner.nextInt();
                int pointY = scanner.nextInt();

                maxX = Math.max(pointX, maxX);
                maxY = Math.max(pointY, maxY);

                listOfPoints.add(new Point(pointID, pointX, pointY));
            }

            myFrame = new MyFrame(maxX, maxY, maxFrameSize);
            graph = new Graph(listOfPoints);
            world = new World(graph, populationSi, generationLimit,
                    crossProbab, mutationProbab, drawEvery, printEvery);

            setPrintingBestPathOnShutdown(printBestPathOnShutdown);

            world.run();
        } catch (FileNotFoundException e) {
            log.fatal("Dataset not found! " + e);
        } catch (MissedIdException e) {
            log.fatal("Dataset is not correct. Missing ID on the path");
        }


    }

    private static void readProperties() throws IOException {
        try (InputStream input = Main.class.getResourceAsStream("/genetic.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            maxFrameSize = Integer.parseInt(prop.getProperty("max_frame_size"));
            populationSi = Integer.parseInt(prop.getProperty("population_size"));
            generationLimit = Integer.parseInt(prop.getProperty("generation_limit"));
            crossProbab = Double.parseDouble(prop.getProperty("cross_probab"));
            mutationProbab = Double.parseDouble(prop.getProperty("mutation_probab"));
            dataset = prop.getProperty("dataset_path");
            drawEvery = Integer.parseInt(prop.getProperty("draw_every"));
            printEvery = Integer.parseInt(prop.getProperty("print_every"));
            printBestPathOnShutdown = Boolean.parseBoolean(prop.getProperty("print_best_path_on_shutdown"));
            log.debug("All properties loaded");
        } catch (IOException e) {
            log.fatal(e);
            throw e;
        }
    }

    private static void setPrintingBestPathOnShutdown(boolean isPrinting){
        if(!isPrinting) return;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            try {
                int[] best = world.getBestKnownPath().getPathAsArray();
                for (int aBest : best) {
                    sb.append(aBest).append(" ");
                }
                sb.append("\n");
                log.trace("The best found path: \n" + sb.toString());
                log.debug("The best path: " + world.getBestKnownPath().getPathLength());
            } catch (NoPathGeneratedException e) {
                log.warn(e);
            }
        }, "Shutdown-thread"));
    }

    public static void addLine(int x1, int y1, int x2, int y2, Color color) {
        myFrame.addLine(x1, y1, x2, y2, color);
    }

    public static void clearFrame() {
        myFrame.removeAll();
        myFrame.revalidate();
        myFrame.repaint();
    }

    public static void clearLines() {
        myFrame.clearLines();
    }
}