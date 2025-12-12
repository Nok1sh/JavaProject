package org.project.presentation.view;


public interface BotView {
    void showWelcomeMessage(Long chatId);

    void showUnknownCommand(Long chatId);

    void showFileRequired(Long chatId);

    void showFileSaved(Long chatId);

    void showFileError(Long chatId);

    void showTextMessage(Long chatId, String text);

    void showPhoto(Long chatId, String imagePath, String caption);

    void showMainMenu(Long chatId);
}