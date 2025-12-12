package org.project;


import org.project.presentation.controller.BotController;


public class TgBot {

    public static void main(String[] args) {

        try {
            BotController bot = new BotController();
            bot.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
