package org.project.view.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CreateGraph {

    public static void takeGraph(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (var item : data.entrySet()) {
            dataset.addValue(item.getValue(), "Средний возраст", item.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "График среднего возраста по командам",
                "Команда",
                "Средний возраст",
                dataset
        );

        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.orange);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());

        try {
            Path outputPath = Paths.get("data", "averageAge.png");
            File outputFile = outputPath.toFile();

            int width = 800;
            int height = 600;
            ChartUtils.saveChartAsPNG(outputFile, chart, width, height);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}