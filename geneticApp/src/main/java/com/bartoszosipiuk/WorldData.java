package com.bartoszosipiuk;

import com.bartoszosipiuk.algorithm.World;
import com.bartoszosipiuk.model.Graph;
import lombok.Data;

/**
 * Created by Bartosz Osipiuk on 2018-06-12.
 *
 * @author Bartosz Osipiuk
 */

@Data
public class WorldData {
    private int maxFrameSize;
    private int populationSize;
    private long generationLimit;
    private double crossProbability;
    private double mutationProbability;
    private String dataset;
    private int drawEvery;
    private int printEvery;
    private boolean printBestPathOnShutdown;
    private Graph graph;
    private World world;


}
