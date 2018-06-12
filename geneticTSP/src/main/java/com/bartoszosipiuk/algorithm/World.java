package com.bartoszosipiuk.algorithm;

import com.bartoszosipiuk.model.crossover.*;
import com.bartoszosipiuk.model.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-19.
 *
 * @author Bartosz Osipiuk
 */

public class World {
    private final Logger log = Logger.getLogger(this.getClass());

    private List<GeneticAlgorithmListener> listeners;

    private Graph graph;
    private long generationLimit = Long.MAX_VALUE;
    private final long drawEveryXPoints;
    private final long printEveryXPoints;

    private Path worstCurrentPath = null;
    private Path bestKnownPath = null;
    private long currentGenerationNumber;
    private double averagePathSize;
    private long unchangedGenerations;

    private final int populationSize;
    private double crossoverProbability;
    private double mutationProbability;

    private List<Path> parents;
    private List<Path> selectionPool;
    private List<Path> childs;

    public World(Graph graph, int populationSize,
                 long generationLimit, double crossoverProbability,
                 double mutationProbability, long drawEveryXPoints, long printEveryXPoints) {
        listeners = new LinkedList<>();

        this.graph = graph;
        this.populationSize = populationSize;
        parents = new ArrayList<>(this.populationSize);

        worstCurrentPath = null;
        currentGenerationNumber = 0;
        if (generationLimit == 0) {
            this.generationLimit = Long.MAX_VALUE;
        } else {
            this.generationLimit = generationLimit;
        }

        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;

        this.drawEveryXPoints = drawEveryXPoints;
        this.printEveryXPoints = printEveryXPoints;
    }

    public void run() {
        for (int i = 0; i < this.populationSize; i++) {
            parents.add(i, new Path(this.graph.generateRandomPath(), this.graph));
        }
        updateImportantPathsAndValues();
        updateAllFitnesses();
        unchangedGenerations = 0;
        double oldMutProb = mutationProbability;
        double oldCrossProb = crossoverProbability;
        while (currentGenerationNumber < generationLimit) {
            currentGenerationNumber++;
            logBestResult();
            if ((int) (unchangedGenerations / 10000) == 0) {
                mutationProbability = oldMutProb;
                crossoverProbability = oldCrossProb;
            } else {
                mutationProbability = oldMutProb + ((int) (unchangedGenerations / 10000.0)) / 1000.0;
                crossoverProbability = oldCrossProb - ((int) (unchangedGenerations / 20000.0)) / 1000.0;
            }

            selection();
            evolve();

            updateGeneration();
            updateImportantPathsAndValues();
            updateAllFitnesses();
        }
        logBestResult();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public Path getBestKnownPath() throws NoPathGeneratedException {
        if (bestKnownPath == null) {
            throw new NoPathGeneratedException("Generic model didn't generate the path yet");
        }
        return bestKnownPath;
    }

    public void addListener(GeneticAlgorithmListener listener){
        this.listeners.add(listener);
    }

    private void drawBestPath() {
        for(GeneticAlgorithmListener listener : listeners){
            listener.drawCurrentBestPath(bestKnownPath);
        }
    }

    private void logBestResult() {
        if (printEveryXPoints == 0 || currentGenerationNumber % printEveryXPoints == 1 || currentGenerationNumber == generationLimit) {
            String sb = String.valueOf(currentGenerationNumber) +
                    "\t\t B:" + bestKnownPath.getPathLength() +
                    "\t Av: " + averagePathSize +
                    "\t Un: " + unchangedGenerations +
                    "\t Mut: " + mutationProbability;

            log.debug(sb);
        }
        if (drawEveryXPoints != 0 && currentGenerationNumber % drawEveryXPoints == 1) {
            try {
                drawBestPath();
            } catch (Exception e) {
                log.warn(e);
            }
        }
    }

    private void updateGeneration() {
        parents = new ArrayList<>();
        parents.addAll(childs);
        childs = null;
    }

    private void evolve() {
        int toMutate = (int) (mutationProbability * selectionPool.size());
        double restMutation = (mutationProbability * selectionPool.size()*1.0) - toMutate;
        if (restMutation >= ThreadLocalRandom.current().nextDouble(1)) {
            toMutate++;
        }
        while (toMutate > 0) {
            int mutIndex = ThreadLocalRandom.current().nextInt(selectionPool.size());
            mutate(selectionPool.get(mutIndex));
            selectionPool.remove(mutIndex);
            --toMutate;
        }

        double copyProbab = 1 - crossoverProbability - mutationProbability;
        if (copyProbab > 0) {
            int toCopy = (int) (copyProbab * selectionPool.size());
            double restCopy = (copyProbab * selectionPool.size() * 1.0) - toCopy;
            if (restCopy >= ThreadLocalRandom.current().nextDouble(1)) {
                toCopy++;
            }
            while (toCopy > 0) {
                int copIndex = ThreadLocalRandom.current().nextInt(selectionPool.size());
                copy(selectionPool.get(copIndex));
                selectionPool.remove(copIndex);
                --toCopy;
            }
        }
        for (int i = 0; i < selectionPool.size(); i++) {
            if (i == selectionPool.size() - 1) {
                crossover(selectionPool.get(i), selectionPool.get(0));
                continue;
            }
            crossover(selectionPool.get(i), selectionPool.get(i + 1));
        }
    }

    private void selection() {
        selectionPool = new ArrayList<>(populationSize);
        childs = new ArrayList<>();

        childs.add(new Path(bestKnownPath));
        Path mutatedBest = new Path(bestKnownPath);
        mutatedBest.mutateByCircuitInversion();
        childs.add(mutatedBest);

        RouletteWheel roulette = new RouletteWheel(parents);
        int currSpines = childs.size();
        while (currSpines < parents.size()) {
            selectionPool.add(parents.get(roulette.pickPathIndex()));
            currSpines++;
        }


    }

    private void updateAllFitnesses() {
        double worstLength = worstCurrentPath.getPathLength();
        for (Path p : parents) {
            p.setFitness(worstLength - p.getPathLength());
        }
    }

    private void updateImportantPathsAndValues() {
        Path longestPath = null;
        Path shortestPath = null;
        double sum = 0;
        for (Path curr : parents) {
            sum += curr.getPathLength();
            if (longestPath == null || (longestPath.getPathLength() < curr.getPathLength())) {
                longestPath = curr;
            }
            if (shortestPath == null || (shortestPath.getPathLength() > curr.getPathLength())) {
                shortestPath = curr;
            }
        }

        if (shortestPath == null) {
            return;
        }
        if (bestKnownPath == null || shortestPath.getPathLength() < bestKnownPath.getPathLength()) {

            bestKnownPath = new Path(shortestPath);
            unchangedGenerations = 0;
        } else {
            unchangedGenerations++;
        }
        worstCurrentPath = longestPath;
        averagePathSize = sum / parents.size();
    }

    private void crossover(Path path1, Path path2) {
        List<Integer> path1Arr = path1.getOrderedPointsList();
        List<Integer> path2Arr = path2.getOrderedPointsList();

        Crossover cross = new UHX(path1Arr, path2Arr, graph);

        List<Integer> childList = new ArrayList<>();
        childList.addAll(cross.getChilds());
        childs.add(new Path(childList, graph));
    }

    private void mutate(Path path1) {
        path1.mutateByCircuitInversion();
        childs.add(path1);
    }

    private void copy(Path path1) {
        childs.add(path1);
    }
}
