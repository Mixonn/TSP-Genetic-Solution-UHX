package com.company.graphic;

import javax.swing.*;
import java.awt.*;

import static com.company.Main.GRAPH_SIZE_MULTIPLIER;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

public class MyFrame extends JFrame {
    GraphComponent graph;
    public MyFrame(int x, int y) {
        super("Hello World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(new Dimension(x*GRAPH_SIZE_MULTIPLIER, y*GRAPH_SIZE_MULTIPLIER));
        setPreferredSize(new Dimension(x*GRAPH_SIZE_MULTIPLIER, y*GRAPH_SIZE_MULTIPLIER));
        graph = new GraphComponent();
        add(graph);
        //add(circles);
    }

    public void addLine(int x1, int y1, int x2, int y2, Color color){
        graph.addLine(x1, y1, x2, y2, color);
    }

    public void addCircle(int x, int y, int r){
        graph.addCircle(x, y, r);
    }

    public void clearLines(){
        graph.clearLines();
    }
}
