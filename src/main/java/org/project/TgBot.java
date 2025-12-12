package org.project;


import org.project.model.db.DatabaseManager;
import org.project.presentation.controller.BotController;

import java.sql.SQLException;

public class TgBot {


    public static void main(String[] args) throws SQLException {


        try {
            BotController bot = new BotController();
            bot.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
