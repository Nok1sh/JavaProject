package org.project.presentation.controller;

import org.project.TgBotConfig;
import org.project.presentation.presenter.Presenter;
import org.project.presentation.view.BotView;
import org.project.presentation.service.BotService;
import org.project.presentation.view.keyboard.KeyboardFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotController extends TelegramLongPollingBot implements BotView {

    private final String botToken = TgBotConfig.getBotToken();
    private final String botUsername = TgBotConfig.getBotUsername();
    private final BotService telegramService;
    private final Presenter presenter;

    public BotController() throws Exception {
        this.telegramService = new BotService(this);
        this.presenter = new Presenter(this);
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
                    String text = message.getText();
                    switch (text) {
                        case "/start":
                            presenter.start(chatId);
                            break;
                        case "График среднего возраста":
                            presenter.averageAgeGraphTeam(chatId);
                            break;
                        case "5 самых высоких игроков, самой возрастной команды":
                            presenter.highestPlayers(chatId);
                            break;
                        case "Команда с самым высоким средним возрастом при условиях":
                            presenter.teamWithHighestAverageAge(chatId);
                            break;
                        default:
                            showUnknownCommand(chatId);
                            break;
                    }
                } else if (message.hasDocument()) {
                    handleDocument(message, chatId);
                } else {
                    showTextMessage(chatId, "Отправьте CSV-файл или выберите команду");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showWelcomeMessage(Long chatId) {
        String text = "Привет, я телеграм-бот (всего лишь проект)\nУмею работать с CSV файлами\nЗагрузите CSV файл, если до этого он не был загружен";
        telegramService.sendMessageWithKeyboard(chatId, text, KeyboardFactory.createMainMenuKeyboard());
    }

    @Override
    public void showUnknownCommand(Long chatId) {
        telegramService.sendMessage(chatId, "Неизвестная команда");
    }

    @Override
    public void showFileRequired(Long chatId) {
        telegramService.sendMessage(chatId, "Загрузите CSV файл");
    }

    @Override
    public void showFileSaved(Long chatId) {
        telegramService.sendMessage(chatId, "Файл сохранён");
    }

    @Override
    public void showFileError(Long chatId) {
        telegramService.sendMessage(chatId, "Ошибка при сохранении файла");
    }

    @Override
    public void showTextMessage(Long chatId, String text) {
        telegramService.sendMessage(chatId, text);
    }

    @Override
    public void showPhoto(Long chatId, String imagePath, String caption) {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(new java.io.File(imagePath)))
                .caption(caption)
                .build();
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            telegramService.sendMessage(chatId, "Ошибка при отправке изображения");
        }
    }

    @Override
    public void showMainMenu(Long chatId) {
        telegramService.sendMessageWithKeyboard(chatId, "Выберите действие:", KeyboardFactory.createMainMenuKeyboard());
    }

    private void handleDocument(Message message, Long chatId) {
        Document document = message.getDocument();
        try {
            GetFile getFile = new GetFile(document.getFileId());
            org.telegram.telegrambots.meta.api.objects.File fileMeta = execute(getFile);
            String filePath = fileMeta.getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

            presenter.processCSVFile(chatId, document.getFileName(), fileUrl);
        } catch (Exception e) {
            showFileError(chatId);
            e.printStackTrace();
        }
    }
}