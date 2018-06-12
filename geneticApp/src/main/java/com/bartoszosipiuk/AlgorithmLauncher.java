package com.bartoszosipiuk;


import com.bartoszosipiuk.algorithm.World;
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


public class AlgorithmLauncher{
    private static final Logger log = Logger.getLogger(AlgorithmLauncher.class);
    private static WorldData worldData;

    public static void main(String[] args) {
        worldData = new WorldData();
        try {
            readProperties();
        } catch (IOException e) {
            return;
        }

        try (Scanner scanner = new Scanner(new File(worldData.getDataset()))) {
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

            MyFrame myFrame = new MyFrame(maxX, maxY, worldData.getMaxFrameSize());
            AlgorithmListener algorithmListener = new AlgorithmListener(myFrame);
            worldData.setGraph(new Graph(listOfPoints));
            worldData.setWorld(new World(worldData.getGraph(), worldData.getPopulationSize(), worldData.getGenerationLimit(),
                    worldData.getCrossProbability(), worldData.getMutationProbability(), worldData.getDrawEvery(), worldData.getPrintEvery()));

            setPrintingBestPathOnShutdown(worldData.isPrintBestPathOnShutdown());

            worldData.getWorld().addListener(algorithmListener);

            worldData.getWorld().run();
        } catch (FileNotFoundException e) {
            log.fatal("Dataset not found! " + e);
        } catch (MissedIdException e) {
            log.fatal("Dataset is not correct. Missing ID on the path");
        }


    }

    private static void readProperties() throws IOException {
        try (InputStream input = AlgorithmLauncher.class.getResourceAsStream("/genetic.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            worldData.setMaxFrameSize(Integer.parseInt(prop.getProperty("max_frame_size")));
            worldData.setPopulationSize(Integer.parseInt(prop.getProperty("population_size")));
            worldData.setGenerationLimit(Integer.parseInt(prop.getProperty("generation_limit")));
            worldData.setCrossProbability(Double.parseDouble(prop.getProperty("cross_probab")));
            worldData.setMutationProbability(Double.parseDouble(prop.getProperty("mutation_probab")));
            worldData.setDataset(prop.getProperty("dataset_path"));
            worldData.setDrawEvery(Integer.parseInt(prop.getProperty("draw_every")));
            worldData.setPrintEvery(Integer.parseInt(prop.getProperty("print_every")));
            worldData.setPrintBestPathOnShutdown(Boolean.parseBoolean(prop.getProperty("print_best_path_on_shutdown")));
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
                int[] best = worldData.getWorld().getBestKnownPath().getPathAsArray();
                for (int aBest : best) {
                    sb.append(aBest).append(" ");
                }
                sb.append("\n");
                log.trace("The best found path: \n" + sb.toString());
                log.debug("The best path: " + worldData.getWorld().getBestKnownPath().getPathLength());
            } catch (NoPathGeneratedException e) {
                log.warn(e);
            }
        }, "Shutdown-thread"));
    }


}