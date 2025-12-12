package org.project.presentation.presenter;

import org.project.model.calculate.Resolver;
import org.project.model.db.DatabaseManager;
import org.project.model.parser.CsvParser;
import org.project.model.graph.CreateGraph;
import org.project.presentation.view.BotView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class Presenter {
    private final BotView view;
    private Resolver resolver;
    private static final Path DATA_DIR = Path.of("data").toAbsolutePath();
    private static final Path CSV_FILE = DATA_DIR.resolve("table.csv");
    private static final Path GRAPH_FILE = DATA_DIR.resolve("averageAge.png");

    public Presenter(BotView view) throws SQLException {
        this.view = view;
        this.resolver = new Resolver();
    }

    public void start(Long chatId) {
        view.showWelcomeMessage(chatId);
    }

    public void averageAgeGraphTeam(Long chatId) throws IOException {
        if (!checkDBExist()) {
            view.showFileRequired(chatId);
            return;
        }
        if (!Files.exists(GRAPH_FILE)) {
            try {
                CreateGraph.takeGraph(resolver.calculateAverageAge());
            } catch (Exception e) {
                view.showTextMessage(chatId, "Не удалось построить график");
                e.printStackTrace();
                return;
            }
        }
        if (Files.exists(GRAPH_FILE) && Files.size(GRAPH_FILE) > 0) {
            view.showPhoto(chatId, GRAPH_FILE.toString(), "График среднего возраста по командам");
        } else {
            view.showTextMessage(chatId, "Не удалось создать изображение");
        }
    }

    public void highestPlayers(Long chatId) {
        if (!checkDBExist()) {
            view.showFileRequired(chatId);
            return;
        }
        var players = resolver.calculate5HighestPlayer();
        String namesPlayers = players.stream()
                .map(player -> player.name() + " - height " + player.height() + " inches")
                .reduce("", (a, b) -> a + "\n" + b);
        view.showTextMessage(chatId, namesPlayers);
    }

    public void teamWithHighestAverageAge(Long chatId) {
        if (!checkDBExist()) {
            view.showFileRequired(chatId);
            return;
        }
        String text = resolver.getTeamWithHighestAverageAge();
        view.showTextMessage(chatId, text);
    }

    public void processCSVFile(Long chatId, String fileName, String fileUrl) {
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            view.showFileError(chatId);
            return;
        }

        try {
            URL url = new URL(fileUrl);
            Files.copy(url.openStream(), CSV_FILE, StandardCopyOption.REPLACE_EXISTING);

            DatabaseManager.getPlayersDao().deleteBuilder().delete();
            DatabaseManager.getTeamsDao().deleteBuilder().delete();
            DatabaseManager.getPositionsDao().deleteBuilder().delete();

            new CsvParser(CSV_FILE.toString()).parseCsv();

            this.resolver = new Resolver();

            view.showFileSaved(chatId);
            view.showMainMenu(chatId);
        } catch (IOException | SQLException e) {
            view.showFileError(chatId);
            e.printStackTrace();
        }
    }

    private boolean checkDBExist() {
        try {
            return !DatabaseManager.isPlayersTableEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}