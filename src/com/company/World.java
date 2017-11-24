package com.company;

import com.company.crossover.CX;
import com.company.crossover.CrossoverPMX;
import com.company.crossover.OX;
import com.company.crossover.PathCrossover;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Bartosz Osipiuk on 2017-11-19.
 *
 * @author Bartosz Osipiuk
 */

public class World {
    private Graph graph;
    private long generationLimit = Long.MAX_VALUE;
    private long DRAW_EVERY_X_POINTS=0;
    private long PRINT_EVERY_X_POINTS=0;

    private Path worstCurrentPath = null;
    private Path bestKnownPath = null;
    private long currentGenerationNumber;
    private double averagePathSize;

    private final int POPULATION_SIZE;
    private final String CROSSOVER_METHOD;
    private final double CROSSOVER_PROBABILITY;
    private final double MUTATION_PROBABILITY;

    private List<Path> parents;
    private List<Path> selectionPool;
    private List<Path> childs;

    public World(Graph graph, int populationSize,
                  long generationLimit, String crossoverMethod, double crossoverProbability,
                  double mutationProbability){
        this.graph=graph;
        this.POPULATION_SIZE = populationSize;
        this.CROSSOVER_METHOD = crossoverMethod;

        parents = new ArrayList<>(POPULATION_SIZE);

        worstCurrentPath = null;
        currentGenerationNumber = 0;
        this.generationLimit = generationLimit;

        CROSSOVER_PROBABILITY = crossoverProbability;
        MUTATION_PROBABILITY = mutationProbability;

        for(int i = 0; i< POPULATION_SIZE; i++){
            parents.add(i, this.graph.generateRandomPath());
        }

        updateImportantPathsAndValues();
        updateAllFitnesses();
    }

    public void run(){
        while(currentGenerationNumber<generationLimit){
            currentGenerationNumber++;
            printBestResult();

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
    private void drawBestPath(){
        Main.clearLines();
        for(int i=0; i<bestKnownPath.getPath().size();i++){
            int temp=bestKnownPath.getPathAt(i);
            int tempNext;
            if(i==bestKnownPath.getPath().size()-1){
                tempNext = bestKnownPath.getPathAt(0);
            }else{
                tempNext = bestKnownPath.getPathAt(i+1);
            }
            Point p = graph.getPointAt(temp);
            Point pNext = graph.getPointAt(tempNext);
            Main.addLine(p.getX(), p.getY(), pNext.getX(), pNext.getY(), Color.BLACK);
        }
    }
    private void printBestResult(){
        if(PRINT_EVERY_X_POINTS==0||currentGenerationNumber%PRINT_EVERY_X_POINTS==1||currentGenerationNumber==generationLimit){
            System.out.print(currentGenerationNumber);
            System.out.print("\t\t B:" + bestKnownPath.getPathLength());
            System.out.println("\t Av: " + averagePathSize);
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
        for(Path p:childs){
            try {
                parents.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        childs = null;
    }

    private void evolve(){
        for(int i=0; i<selectionPool.size(); i+=2){
            if(i==selectionPool.size()-1){
                childs.add(selectionPool.get(i));
                continue;
            }
            double evolveChoise = ThreadLocalRandom.current().nextDouble();

            //Crossover
            if((evolveChoise>=0.0)&&(evolveChoise<=CROSSOVER_PROBABILITY)){
                crossover(selectionPool.get(i), selectionPool.get(i+1));
            }else if((evolveChoise>CROSSOVER_PROBABILITY)&&(evolveChoise<=CROSSOVER_PROBABILITY+MUTATION_PROBABILITY)){
                mutate(selectionPool.get(i), selectionPool.get(i+1));
            }else{
                copy(selectionPool.get(i), selectionPool.get(i+1));
            }


        }
    }

    //Done
    private void selection() {
        selectionPool = new ArrayList<>(POPULATION_SIZE);
        childs = new ArrayList<>();
        childs.add(bestKnownPath);

        RouletteWheel roulette = new RouletteWheel((ArrayList<Path>)parents);
        int currSpines = 1; //We added the best value to childs
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
        if(bestKnownPath==null || shortestPath.getPathLength() < bestKnownPath.getPathLength()){ //GOD, i know it can generate null, but it doesnt
            bestKnownPath = shortestPath;
        }
        worstCurrentPath = longestPath;
        averagePathSize = sum/parents.size();
    }

    private void crossover(Path path1, Path path2) {
        int[] path1Arr = path1.getPathAsArray();
        int[] path2Arr = path2.getPathAsArray();
        PathCrossover method;
        if(CROSSOVER_METHOD.toLowerCase().equals("ox")){
            method = new OX(path1Arr, path2Arr);
        }else if(CROSSOVER_METHOD.toLowerCase().equals("cx")){
            method = new CX(path1Arr, path2Arr);
        }else {
            method = new CrossoverPMX(path1Arr, path2Arr);
        }

        List<Integer> ar1 = new ArrayList<>();
        List<Integer> ar2= new ArrayList<>();
        for(int i=0; i<path1Arr.length; i++){
            ar1.add(method.getOffspring1()[i]);
            ar2.add(method.getOffspring2()[i]);
        }

        childs.add(new Path(ar1, graph));
        childs.add(new Path(ar2, graph));
    }

    private void mutate(Path path1, Path path2){
        path1.mutateByCircuitInversion();
        path2.mutateByCircuitInversion();
        childs.add(path1);
        childs.add(path2);
    }

    private void copy(Path path1, Path path2){
        childs.add(path1);
        childs.add(path2);
    }



}
