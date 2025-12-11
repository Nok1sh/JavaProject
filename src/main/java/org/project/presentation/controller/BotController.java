package org.project.presentation.controller;

import org.project.TgBotConfig;
import org.project.model.calculate.Resolver;
import org.project.model.db.DatabaseManager;
import org.project.model.parser.CsvParser;
import org.project.presentation.service.BotService;

import org.project.view.graph.CreateGraph;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.io.InputStream;
import java.sql.SQLException;


public class BotController extends TelegramLongPollingBot {

    private final String botToken = TgBotConfig.getBotToken();
    private final String botUsername = TgBotConfig.getBotUsername();
    private final BotService telegramService;
    private static final Path DATA_DIR = Path.of("data").toAbsolutePath();
    private Resolver resolver = new Resolver();

    static {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать папку 'data'", e);
        }
    }

    public BotController() throws SQLException {
        this.telegramService = new BotService(this);
    }

    public void start() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
        System.out.println("Бот работает");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();

                if (message.hasText()) {
                    handleMessage(message);
                } else if (message.hasDocument()) {
                    handleCSV(message);
                } else {
                    telegramService.sendMessage(chatId, "Отправьте CSV-файл");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) throws SQLException {
        String messageText = message.getText();
        Long chatId = message.getChatId();

        switch (messageText) {
            case "/start":
                sendWelcomeMessage(chatId);
                break;
            case "График среднего возраста":
                getAverageAgeImage(chatId);
                break;
            case "5 самых высоких игроков, самой возрастной команды":
                HighestPlayer(chatId);
                break;
            case "Команда с самым высоким средним возрастом при условиях":
                teamWithHighestAverageAge(chatId);
                break;
            default:
                sendUnknownCommand(chatId);
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        String text = " Привет, я телеграм-бот (всего лишь проект)\n Умею работать с CSV файлами (но только с одним)\n Загрузите CSV файл, если до этого он не был загружен";
        telegramService.sendMessageWithKeyboard(chatId, text, KeyboardFactory.createMainMenuKeyboard());
    }

    private void teamWithHighestAverageAge(Long chatId) throws SQLException {
        if (!checkExistDB(chatId)) {
            return;
        }
        String text = resolver.getTeamWithHighestAverageAge();
        telegramService.sendMessage(chatId, text);
    }

    private void HighestPlayer(Long chatId) throws SQLException {
        if (!checkExistDB(chatId)) {
            return;
        }
        var players = resolver.calculate5HighestPlayer();
        String namesPlayers = players.stream()
                .map(player -> player.name() + " - height " + player.height() + " inches")
                .reduce("", (a, b) -> a + "\n" + b);
        telegramService.sendMessage(chatId, namesPlayers);
    }

    private void handleCSV(Message message) {
        Document document = message.getDocument();
        Long chatId = message.getChatId();

        String fileName = document.getFileName();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            telegramService.sendMessage(chatId, "Отправьте файл с расширением .csv");
            return;
        }

        try {
            String fileId = document.getFileId();

            org.telegram.telegrambots.meta.api.objects.File fileMeta = execute(new GetFile(fileId));
            String filePath = fileMeta.getFilePath();

            URL fileUrl = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);

            Path localFile = DATA_DIR.resolve("table.csv");

            try (InputStream in = fileUrl.openStream()) {
                Files.copy(in, localFile, StandardCopyOption.REPLACE_EXISTING);
            }

            telegramService.sendMessage(chatId, "Файл сохранён");

            new CsvParser("data/table.csv").parseCsv();

        } catch (Exception e) {
            telegramService.sendMessage(chatId, "Ошибка при сохранении файла");
            e.printStackTrace();
        }
    }

    private void getAverageAgeImage(Long chatId) throws SQLException {
        if (!checkExistDB(chatId)) {
            return;
        }

        String path = "data/averageAge.png";
        Path imagePath = Path.of(path).toAbsolutePath();

        try {
            if (!Files.exists(imagePath)) {
                CreateGraph.takeGraph(resolver.calculateAverageAge());
            }

            if (Files.exists(imagePath) && Files.size(imagePath) > 0) {
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId.toString())
                        .photo(new InputFile(imagePath.toFile(), "averageAge.png"))
                        .build();
                this.execute(sendPhoto);
            } else {
                telegramService.sendMessage(chatId, "Не удалось создать изображение");
            }
        } catch (Exception e) {
            e.printStackTrace();
            telegramService.sendMessage(chatId, "Ошибка при отправке изображения");
        }
    }

    private void sendUnknownCommand(Long chatId) {
        String text = "Неизвестная команда";
        telegramService.sendMessage(chatId, text);
    }

    private boolean checkExistDB(Long chatId) throws SQLException {
        if (DatabaseManager.isPlayersTableEmpty()){
             telegramService.sendMessage(chatId, "Загрузите файл");
             return false;
        } return true;
    }


    private boolean waitForFile(Path path, long timeoutMs) throws IOException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (Files.exists(path) && Files.size(path) > 0) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
