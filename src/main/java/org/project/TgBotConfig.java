package org.project;

import io.github.cdimascio.dotenv.Dotenv;

public class TgBotConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String getBotToken() {
        String token = dotenv.get("TG_BOT_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Token not found");
        }
        return token;
    }

    public static String getBotUsername() {
        String username = dotenv.get("TG_BOT_USERNAME");
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("Username not found");
        }
        return username;
    }
}
