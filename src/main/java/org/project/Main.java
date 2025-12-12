package org.project;


import org.project.model.calculate.Resolver;
import org.project.model.db.DatabaseManager;
import org.project.model.parser.CsvParser;
import org.project.view.graph.CreateGraph;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

//    private static final DatabaseManager databaseManager = new DatabaseManager();

    public static void main(String[] args) throws IOException, SQLException {
//        new CsvParser("data/table.csv").parseCsv();
        Resolver resolver = new Resolver();
//        CreateGraph.takeGraph(resolver.calculateAverageAge());
        System.out.println(resolver.calculate5HighestPlayer());
        System.out.println(resolver.getTeamWithHighestAverageAge());
        System.out.println(resolver.calculateAverageHeight());
        System.out.println(resolver.calculateAverageWeight());
    }
}