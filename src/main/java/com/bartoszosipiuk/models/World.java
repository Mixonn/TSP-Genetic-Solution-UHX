package com.bartoszosipiuk.models;

import com.bartoszosipiuk.Main;
import com.bartoszosipiuk.crossover.*;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-19.
 *
 * @author Bartosz Osipiuk
 */

public class World {
    private final Logger log = Logger.getLogger(this.getClass());

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
                  double mutationProbability, long drawEveryXPoints, long printEveryXPoints){
        this.graph=graph;
        this.populationSize = populationSize;
        parents = new ArrayList<>(this.populationSize);

        worstCurrentPath = null;
        currentGenerationNumber = 0;
        if(generationLimit==0){
            this.generationLimit = Long.MAX_VALUE;
        }else {
            this.generationLimit = generationLimit;
        }

        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;

        this.drawEveryXPoints = drawEveryXPoints;
        this.printEveryXPoints = printEveryXPoints;

        for(int i = 0; i< this.populationSize; i++){
            parents.add(i, new Path(this.graph.generateRandomPath(), this.graph));
        }

        updateImportantPathsAndValues();
        updateAllFitnesses();
    }

    public void run(){
        unchangedGenerations = 0;
        double oldMutProb = mutationProbability;
        double oldCrossProb = crossoverProbability;
        while(currentGenerationNumber<generationLimit){
            currentGenerationNumber++;
            printBestResult();
            if((int)(unchangedGenerations/10000) == 0){
                mutationProbability = oldMutProb;
                crossoverProbability = oldCrossProb;
            }else{
                mutationProbability = oldMutProb+ ((int)(unchangedGenerations/10000.0))/1000.0;
                crossoverProbability = oldCrossProb - ((int)(unchangedGenerations/20000.0))/1000.0;
            }

            selection();
            evolve();

            updateGeneration();
            updateImportantPathsAndValues();
            updateAllFitnesses();
        }
        printBestResult();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public Path getBestKnownPath() throws  NoPathGeneratedException{
        if(bestKnownPath==null){
            throw new NoPathGeneratedException("Generic algorithm didn't generate the path yet");
        }
        return bestKnownPath;
    }


    private void drawBestPath(){
        Main.clearLines();
        for(int i=0; i<bestKnownPath.getPath().size();i++){
            int temp=bestKnownPath.getIdAt(i);
            int tempNext;
            if(i==bestKnownPath.getPath().size()-1){
                tempNext = bestKnownPath.getIdAt(0);
            }else{
                tempNext = bestKnownPath.getIdAt(i+1);
            }
            com.bartoszosipiuk.models.Point p = graph.getPointAt(temp);
            Point pNext = graph.getPointAt(tempNext);
            Main.addLine(p.getX(), p.getY(), pNext.getX(), pNext.getY(), Color.BLACK);
        }
    }
    private void printBestResult(){
        if(printEveryXPoints ==0||currentGenerationNumber% printEveryXPoints ==1||currentGenerationNumber==generationLimit){
            String sb = String.valueOf(currentGenerationNumber) +
                    "\t\t B:" + bestKnownPath.getPathLength() +
                    "\t Av: " + averagePathSize +
                    "\t Un: " + unchangedGenerations +
                    "\t Mut: " + mutationProbability;

            log.debug(sb);
        }
        if(drawEveryXPoints !=0 && currentGenerationNumber% drawEveryXPoints ==1){
            try{
                drawBestPath();
            }catch (Exception e){
                log.warn(e);
            }
        }
    }
    private void updateGeneration(){
        parents = new ArrayList<>();
        parents.addAll(childs);
        childs = null;
    }

    private void saveBestResult(int generation){
        try(FileWriter fw = new FileWriter("pathsResults.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)){
            out.print(generation + " ");
            out.print(bestKnownPath.getPathLength()+" ");
            out.print( bestKnownPath );
            out.println("]");
        } catch (IOException e) {
            log.warn(e + "\nCannot save results to the file");
        }
    }

    private void evolve(){
        int toMutate = (int)(mutationProbability * selectionPool.size());
        double restMutation = (mutationProbability * selectionPool.size())-toMutate;
        if(restMutation>=ThreadLocalRandom.current().nextDouble(1)){
            toMutate++;
        }
        while(toMutate>0){
            int mutIndex = ThreadLocalRandom.current().nextInt(selectionPool.size());
            mutate(selectionPool.get(mutIndex));
            selectionPool.remove(mutIndex);
            --toMutate;
        }

        double copyProbab = 1- crossoverProbability - mutationProbability;
        if(copyProbab>0){
            int toCopy = (int)(copyProbab * selectionPool.size());
            double restCopy = (copyProbab * selectionPool.size()*1.0)-toCopy;
            if(restCopy>=ThreadLocalRandom.current().nextDouble(1)){
                toCopy++;
            }
            while(toCopy>0){
                int copIndex = ThreadLocalRandom.current().nextInt(selectionPool.size());
                copy(selectionPool.get(copIndex));
                selectionPool.remove(copIndex);
                --toCopy;
            }
        }
        for(int i=0; i<selectionPool.size(); i++){
            if(i==selectionPool.size()-1){
                crossover(selectionPool.get(i), selectionPool.get(0));
                continue;
            }
            crossover(selectionPool.get(i), selectionPool.get(i+1));
        }
    }

    private void selection() {
        selectionPool = new ArrayList<>(populationSize);
        childs = new ArrayList<>();
        try {
            childs.add(bestKnownPath.clone());
            Path mutatedBest = bestKnownPath.clone();
            mutatedBest.mutateByCircuitInversion();
            childs.add(mutatedBest);
        } catch (CloneNotSupportedException e) {
            log.error(e);
        }

        RouletteWheel roulette = new RouletteWheel(parents);
        int currSpines = childs.size();
        while(currSpines<parents.size()){
            selectionPool.add(parents.get(roulette.pickPathIndex()));
            currSpines++;
        }


    }

    private void updateAllFitnesses(){
        double worstLength = worstCurrentPath.getPathLength();
        for(Path p:parents){
            p.setFitness(worstLength-p.getPathLength());
        }
    }

    private void updateImportantPathsAndValues(){
        Path longestPath = null;
        Path shortestPath = null;
        double sum=0;
        for (Path curr : parents) {
            sum += curr.getPathLength();
            if (longestPath == null || (longestPath.getPathLength() < curr.getPathLength())) {
                longestPath = curr;
            }
            if (shortestPath == null || (shortestPath.getPathLength() > curr.getPathLength())) {
                shortestPath = curr;
            }
        }

        if(shortestPath==null){
            return;
        }
        if(bestKnownPath==null || shortestPath.getPathLength() < bestKnownPath.getPathLength()){
            try {
                bestKnownPath = shortestPath.clone();
            } catch (CloneNotSupportedException e) {
                log.error(e);
            }
            unchangedGenerations=0;
        }else{
            unchangedGenerations++;
        }
        worstCurrentPath = longestPath;
        averagePathSize = sum/parents.size();
    }

    private void crossover(Path path1, Path path2) {
        List<Integer> path1Arr = path1.getPath();
        List<Integer> path2Arr = path2.getPath();

        Crossover cross = new UHX(path1Arr, path2Arr, graph);

        List<Integer> childList = new ArrayList<>();
        childList.addAll(cross.getChilds());
        childs.add(new Path(childList, graph));
    }

    private void mutate(Path path1){
        path1.mutateByCircuitInversion();
        childs.add(path1);
    }

    private void copy(Path path1){
        childs.add(path1);
    }
}
