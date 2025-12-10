package org.project.view.graph;

import javax.swing.*;
import java.util.Map;

public class CreateGraph {
    public static void takeGraph(Map<String, Double> data){
        SwingUtilities.invokeLater(() -> {
            BarChart example = new BarChart(data);
            example.setSize(800, 600);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            example.setVisible(true);
        });
    }
}
