package com.company.models;

import com.company.Main;
import com.company.crossover.*;
import com.company.models.Graph;
import com.company.models.Path;
import com.company.models.Point;
import com.company.models.RouletteWheel;
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
    protected final Logger log = Logger.getLogger(this.getClass());

    private Graph graph;
    private long generationLimit = Long.MAX_VALUE;
    private long DRAW_EVERY_X_POINTS=0;
    private long PRINT_EVERY_X_POINTS=0;

    private Path worstCurrentPath = null;
    private Path bestKnownPath = null;
    private long currentGenerationNumber;
    private double averagePathSize;
    private long unchangedGenerations;

    private final int POPULATION_SIZE;
    private double CROSSOVER_PROBABILITY;
    private double MUTATION_PROBABILITY;

    private List<Path> parents;
    private List<Path> selectionPool;
    private List<Path> childs;

    public World(Graph graph, int populationSize,
                  long generationLimit, double crossoverProbability,
                  double mutationProbability){
        this.graph=graph;
        this.POPULATION_SIZE = populationSize;
        parents = new ArrayList<>(POPULATION_SIZE);

        worstCurrentPath = null;
        currentGenerationNumber = 0;
        if(generationLimit==0){
            this.generationLimit = Long.MAX_VALUE;
        }else {
            this.generationLimit = generationLimit;
        }

        CROSSOVER_PROBABILITY = crossoverProbability;
        MUTATION_PROBABILITY = mutationProbability;

        for(int i = 0; i< POPULATION_SIZE; i++){
            parents.add(i, this.graph.generateRandomPath());
        }

        updateImportantPathsAndValues();
        updateAllFitnesses();
    }

    public void run(){
        unchangedGenerations = 0;
        double oldMutProb = MUTATION_PROBABILITY;
        double oldCrossProb = CROSSOVER_PROBABILITY;
        while(currentGenerationNumber<generationLimit){
            currentGenerationNumber++;
            printBestResult();
            if((int)(unchangedGenerations/10000) == 0){
                MUTATION_PROBABILITY = oldMutProb;
                CROSSOVER_PROBABILITY = oldCrossProb;
            }else{
                MUTATION_PROBABILITY = oldMutProb+ ((int)(unchangedGenerations/10000.0))/1000.0;
                CROSSOVER_PROBABILITY = oldCrossProb - ((int)(unchangedGenerations/20000.0))/1000.0;
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

    public void drawEveryXGenerations(long i){
        DRAW_EVERY_X_POINTS = i;
    }
    public void printResultEveryXGenerations(long i){
        PRINT_EVERY_X_POINTS = i;
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
            com.company.models.Point p = graph.getPointAt(temp);
            Point pNext = graph.getPointAt(tempNext);
            Main.addLine(p.getX(), p.getY(), pNext.getX(), pNext.getY(), Color.BLACK);
        }
    }
    private void printBestResult(){
        if(PRINT_EVERY_X_POINTS==0||currentGenerationNumber%PRINT_EVERY_X_POINTS==1||currentGenerationNumber==generationLimit){
            log.debug(currentGenerationNumber);
            log.debug("\t\t B:" + bestKnownPath.getPathLength());
            log.debug("\t Av: " + averagePathSize);
            log.debug("\t Un: " + unchangedGenerations);
            log.debug("\t Mut: " + MUTATION_PROBABILITY);
        }
        if(DRAW_EVERY_X_POINTS!=0 && currentGenerationNumber%DRAW_EVERY_X_POINTS==1){
            try{
                drawBestPath();
            }catch (Exception e){
                e.printStackTrace();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evolve(){
        int toMutate = (int)(MUTATION_PROBABILITY * selectionPool.size());
        double restMutation = (MUTATION_PROBABILITY * selectionPool.size())-toMutate;
        if(restMutation>=ThreadLocalRandom.current().nextDouble(1)){
            toMutate++;
        }
        while(toMutate>0){
            int mutIndex = ThreadLocalRandom.current().nextInt(selectionPool.size());
            mutate(selectionPool.get(mutIndex));
            selectionPool.remove(mutIndex);
            --toMutate;
        }

        double copyProbab = 1-CROSSOVER_PROBABILITY-MUTATION_PROBABILITY;
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

    //Done
    private void selection() {
        selectionPool = new ArrayList<>(POPULATION_SIZE);
        childs = new ArrayList<>();
        try {
            childs.add(bestKnownPath.clone());
            Path mutatedBest = bestKnownPath.clone();
            mutatedBest.mutateByCircuitInversion();
            childs.add(mutatedBest);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        RouletteWheel roulette = new RouletteWheel((ArrayList<Path>)parents);
        int currSpines = childs.size();
        while(currSpines<parents.size()){
            selectionPool.add(parents.get(roulette.pickPathIndex()));
            currSpines++;
        }


    }
    //Done
    private void updateAllFitnesses(){
        double worstLength = worstCurrentPath.getPathLength();
        for(Path p:parents){
            p.setFitness(worstLength-p.getPathLength());
        }
    }

    //Done
    private void updateImportantPathsAndValues(){
        Path longestPath = null;
        Path shortestPath = null;
        double sum=0;
        for(int i = 0; i< parents.size(); i++){
            Path curr = parents.get(i);
            sum+=parents.get(i).getPathLength();
            if(longestPath==null || (longestPath.getPathLength()<curr.getPathLength())){
                longestPath = curr;
            }
            if(shortestPath==null || (shortestPath.getPathLength()>curr.getPathLength())){
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
                e.printStackTrace();
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
