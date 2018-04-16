package com.bartoszosipiuk.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Bartosz Osipiuk on 2017-11-20.
 *
 * @author Bartosz Osipiuk
 */

public class MyFrame extends JFrame {
    private GraphComponent graph;
    private final double multiplier;

    public MyFrame(int x, int y, int maxFrameSize) {
        super("Hello World");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        multiplier = (x > y) ? (maxFrameSize - 10) * 1.0 / x : (maxFrameSize - 10) * 1.0 / y;

        setSize(new Dimension((int) (x * multiplier) + 10, (int) (y * multiplier) + 10));
        setPreferredSize(new Dimension((int) (x * multiplier) + 10, (int) (y * multiplier) + 10));
        graph = new GraphComponent();
        add(graph);
    }

    public void addLine(int x1, int y1, int x2, int y2, Color color) {
        graph.addLine((int) (x1 * multiplier), (int) (y1 * multiplier), (int) (x2 * multiplier), (int) (y2 * multiplier), color);
    }

    public void addCircle(int x, int y, int r) {
        graph.addCircle((int) (x * multiplier), (int) (y * multiplier), r);
    }

    public void clearLines() {
        graph.clearLines();
    }
}
