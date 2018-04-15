package com.bartoszosipiuk.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

public class MyFrame extends JFrame {
    GraphComponent graph;
    private final double MULTIPLIER;
    public MyFrame(int x, int y, int maxFrameSize) {
        super("Hello World");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        MULTIPLIER = (x>y)?(maxFrameSize-10)*1.0/x:(maxFrameSize-10)*1.0/y;

        setSize(new Dimension((int)(x*MULTIPLIER)+10, (int)(y*MULTIPLIER)+10));
        setPreferredSize(new Dimension((int)(x*MULTIPLIER)+10, (int)(y*MULTIPLIER)+10));
        graph = new GraphComponent();
        add(graph);
        //add(circles);
    }

    public void addLine(int x1, int y1, int x2, int y2, Color color){
        graph.addLine((int)(x1*MULTIPLIER), (int)(y1*MULTIPLIER), (int)(x2*MULTIPLIER), (int)(y2*MULTIPLIER), color);
    }

    public void addCircle(int x, int y, int r){
        graph.addCircle((int)(x*MULTIPLIER), (int)(y*MULTIPLIER), r);
    }

    public void clearLines(){
        graph.clearLines();
    }
}
