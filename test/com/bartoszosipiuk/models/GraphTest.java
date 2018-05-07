package com.bartoszosipiuk.models;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class GraphTest {

    static Graph graph;
    static List<Point> points;

    @BeforeClass
    static public void beforeClass(){
        points = new ArrayList<>();
        for(int i=0; i<3; i++){
            Point p = mock(Point.class);
            when(p.distance(anyObject())).thenReturn(2.0);
            when(p.getId()).thenReturn(i);
            points.add(p);
        }
    }

    @Before
    public void setUp() throws MissedIdException {
        graph = new Graph(points);
    }

    @Test
    public void shouldThrowExceptionWhenIdOnPathIsMissing() {
        ArrayList<Point> tmp = new ArrayList<>();
        tmp.add(points.get(1));
        try {
            graph = new Graph(tmp);
            fail("The MissedIdException should be thrown when there is missing id in the path");
        } catch (MissedIdException e) {
        }
    }

    @Test
    public void shouldReturnPointOfPassedId(){
        assertEquals(1, graph.getPointAt(1).getId());
    }

    @Test
    public void shouldGenerateRandomPathWithAllPoints(){
        assertEquals("Generated path should be this same length as much point we have",
                3, graph.generateRandomPath().size());
    }

    @Test
    public void shouldReturnCorrectDistanceBetweenTwoPoints(){
        assertEquals(2, graph.getDistance(1,2), 0);
    }
}