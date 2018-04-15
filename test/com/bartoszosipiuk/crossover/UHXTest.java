package com.bartoszosipiuk.crossover;

import com.bartoszosipiuk.models.Graph;
import com.bartoszosipiuk.models.Path;
import com.bartoszosipiuk.models.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UHXTest {
    static Graph graph;
    static Path p1, p2;
    static UHX uhx;
    static Path result;

    @BeforeAll
    static void setUp(){
        List<Point> listOfPoints = new LinkedList<>();

        try(Scanner scanner = new Scanner(new File("C:\\Projekty\\Java\\TSPGeneticNew\\TSP_Generic_Solution\\resources\\test.tsp"))){
            int numberOfPoints = scanner.nextInt();
            for (int i = 0; i < numberOfPoints; i++) {
                int sID = scanner.nextInt() - 1;
                int sX = scanner.nextInt();
                int sY = scanner.nextInt();
                listOfPoints.add(new Point(sID, sX, sY));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        graph = new Graph(listOfPoints);
        p1=graph.generateRandomPath();
        p2=graph.generateRandomPath();

        uhx = new UHX(p1.getPath(), p2.getPath(), graph);
        List<Integer> childList = new ArrayList<>();
        childList.addAll(uhx.getChilds());

        result = new Path(childList, graph);
    }



    @Test
    void getChildReturnsCorrectPath() {
        if(result.getPathSize()!=p1.getPathSize()){
            fail("Child and parent size is not equal");
        }


    }

    @Test
    void isHamiltonPath(){
        HashSet<Integer> p = new HashSet<>();
        for(int i=0; i<result.getPathSize()-1; i++){
            if(p.contains(i)){
                fail("Path contains the same point 2 or more times");
            }else{
                p.add(i);
            }
        }
    }

}