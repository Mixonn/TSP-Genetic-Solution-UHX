package com.bartoszosipiuk.crossover;

import com.bartoszosipiuk.model.Graph;

import java.util.*;

/**
 * Created by Bartosz Osipiuk on 2017-12-04.
 *
 * @author Bartosz Osipiuk
 */
public class UHX implements Crossover {
    private enum MinDistFlag {
        ERROR,
        BACK_FATHER,
        BACK_MOTHER,
        FRONT_FATHER,
        FRONT_MOTHER
    }

    private int bF;
    private int bM;
    private int fF;
    private int fM;

    private List<Integer> father;
    private List<Integer> mother;
    private final int fatherSize;

    private Graph graph;

    private Set<Integer> childs;

    private int currentPointIndex;

    public UHX(List<Integer> p1, List<Integer> p2, Graph graph) {
        father = p1;
        mother = p2;
        fatherSize = father.size();

        this.graph = graph;

        run();
    }

    private void run() {
        SplittableRandom rand = new SplittableRandom();
        currentPointIndex = rand.nextInt(father.size());
        childs = new LinkedHashSet<>();

        setPointers(currentPointIndex);
        childs.add(currentPointIndex);

        while (childs.size() < fatherSize) {
            addTheShortestPathToNewGeneration(checkTheShortestOption());
        }

    }

    private void addTheShortestPathToNewGeneration(MinDistFlag flag) {
        if (flag == MinDistFlag.ERROR) {
            throw new IllegalStateException("Finding minimum distance fail");
        } else if (flag == MinDistFlag.BACK_FATHER) {
            if (!childs.contains(father.get(bF))) {
                childs.add(father.get(bF));
                currentPointIndex = father.get(bF);
            }
            backFather();
        } else if (flag == MinDistFlag.FRONT_FATHER) {
            if (!childs.contains(father.get(fF))) {
                childs.add(father.get(fF));
                currentPointIndex = father.get(fF);
            }
            frontFather();
        } else if (flag == MinDistFlag.BACK_MOTHER) {
            if (!childs.contains(mother.get(bM))) {
                childs.add(mother.get(bM));
                currentPointIndex = mother.get(bM);
            }
            backMother();
        } else {
            if (!childs.contains(mother.get(fM))) {
                childs.add(mother.get(fM));
                currentPointIndex = mother.get(fM);
            }
            frontMother();
        }
    }


    private MinDistFlag checkTheShortestOption() {
        MinDistFlag flag = MinDistFlag.ERROR;
        double minDist = Double.MAX_VALUE;
        if (graph.getDistance(currentPointIndex, father.get(bF)) < minDist) {
            minDist = graph.getDistance(currentPointIndex, father.get(bF));
            flag = MinDistFlag.BACK_FATHER;
        }
        if (graph.getDistance(currentPointIndex, father.get(fF)) < minDist) {
            minDist = graph.getDistance(currentPointIndex, father.get(fF));
            flag = MinDistFlag.FRONT_FATHER;
        }
        if (graph.getDistance(currentPointIndex, mother.get(bM)) < minDist) {
            minDist = graph.getDistance(currentPointIndex, mother.get(bM));
            flag = MinDistFlag.BACK_MOTHER;
        }
        if (graph.getDistance(currentPointIndex, mother.get(fM)) < minDist) {
            flag = MinDistFlag.FRONT_MOTHER;
        }
        return flag;
    }

    public Set<Integer> getChilds() {
        return childs;
    }

    private void setPointers(int c) {
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


    private void backFather() {
        int i = bF - 1;
        bF = (i < 0) ? father.size() - 1 : i;

    }

    private void frontFather() {
        int i = fF + 1;
        fF = (i >= father.size()) ? 0 : i;
    }

    private void backMother() {
        int i = bM - 1;
        bM = (i < 0) ? mother.size() - 1 : i;
    }

    private void frontMother() {
        int i = fM + 1;
        fM = (i >= mother.size()) ? 0 : i;
    }
}
