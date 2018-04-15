package com.company.crossover;

import com.company.models.Graph;

import java.util.*;

/**
 * Created by Bartosz Osipiuk on 2017-12-04.
 *
 * @author Bartosz Osipiuk
 */
public class UHX implements Crossover{
    private int bF;
    private int bM;
    private int fF;
    private int fM;

    private List<Integer> father;
    private List<Integer> mother;

    private Graph graph;

    Set<Integer> childs;

    public UHX(List<Integer> p1, List<Integer> p2, Graph graph){
        father = p1;
        mother = p2;

        this.graph = graph;

        run();
    }
    private void run(){
        SplittableRandom rand = new SplittableRandom();
        int c = rand.nextInt(father.size());
        double[][] distances = graph.getAllDistances();
        childs = new LinkedHashSet<>();

        final int fatherSize=father.size();

        setPointers(c);
        childs.add(c);

        int current = father.indexOf(c);

        while(childs.size()<fatherSize){
            double minDist = Double.MAX_VALUE;
            int flag = -1;
            if(distances[c][father.get(bF)]<minDist){
                minDist=distances[c][father.get(bF)];
                flag=0;
            }
            if(distances[c][father.get(fF)]<minDist){
                minDist=distances[c][father.get(fF)];
                flag=1;
            }
            if(distances[c][mother.get(bM)]<minDist){
                minDist=distances[c][mother.get(bM)];
                flag=2;
            }
            if(distances[c][mother.get(fM)]<minDist){
                flag=3;
            }

            if(flag==-1){
                throw new IllegalStateException("Finding minimum distance fail");
            }else if(flag==0){
                if(childs.contains(father.get(bF))){
                    backFather();
                    continue;
                }
                childs.add(father.get(bF));
                c=father.get(bF);
                backFather();
            }else if(flag==1){
                if(childs.contains(father.get(fF))){
                    frontFather();
                    continue;
                }
                childs.add(father.get(fF));
                c=father.get(fF);
                frontFather();
            }else if(flag==2){
                if(childs.contains(mother.get(bM))){
                    backMother();
                    continue;
                }
                childs.add(mother.get(bM));
                c=mother.get(bM);
                backMother();
            }else{
                if(childs.contains(mother.get(fM))){
                    frontMother();
                    continue;
                }
                childs.add(mother.get(fM));
                c=mother.get(fM);
                frontMother();
            }
        }

    }

    public Set<Integer> getChilds(){
        return childs;
    }

    private void setPointers(int c){
        int fatherIndex = father.indexOf(c);
        int motherIndex = mother.indexOf(c);
        bF = fatherIndex;
        bM = motherIndex;
        fF = fatherIndex;
        fM = motherIndex;
        backFather();
        frontFather();
        backMother();
        frontMother();
    }

    private void backFather(){
        int i = bF -1;
        bF = (i<0)? father.size()-1 : i;

    }
    private void frontFather(){
        int i = fF +1;
        fF = (i>=father.size())? 0 : i;
    }
    private void backMother(){
        int i = bM -1;
        bM = (i<0)? mother.size()-1 : i;
    }
    private void frontMother(){
        int i = fM +1;
        fM = (i>=mother.size())? 0 : i;
    }
}