package org.project;


import org.project.model.db.DatabaseManager;
import org.project.presentation.controller.BotController;

import java.sql.SQLException;

public class TgBot {

    private static final DatabaseManager databaseManager = new DatabaseManager();

    public static void main(String[] args) throws SQLException {

        databaseManager.init();
        
        try {
            BotController bot = new BotController();
            bot.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
