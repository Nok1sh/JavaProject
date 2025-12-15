package org.project.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.model.calculate.IResolver;
import org.project.model.player.Player;
import org.project.model.player.Position;
import org.project.presentation.presenter.Presenter;
import org.project.presentation.view.BotView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class PresenterTest {

    private IResolver resolver;
    private BotView view;
    private Presenter presenter;

    @BeforeEach
    void setUp() throws SQLException {
        resolver = mock(IResolver.class);
        view = mock(BotView.class);
        presenter = new Presenter(view);
    }

    @Test
    void averageAgeGraphTeam_WithExistingGraph_CallsViewWithPhoto() throws IOException {
        when(resolver.calculateAverageAge()).thenReturn(Map.of("TeamA", 27.5, "TeamB", 30.0));
        presenter.averageAgeGraphTeam(12345L);
        verify(view).showPhoto(eq(12345L), anyString(), eq("График среднего возраста по командам"));
    }

    @Test
    void averageAgeGraphTeam_WithoutGraph_CreatesAndShowsPhoto() throws Exception {
        when(resolver.calculateAverageAge()).thenReturn(Map.of("TeamA", 27.5));

        presenter.averageAgeGraphTeam(12345L);

        verify(view).showPhoto(eq(12345L), anyString(), eq("График среднего возраста по командам"));
    }

    @Test
    void highestPlayers_CallsViewWithCorrectData() {
        List<Player> topPlayers = List.of(
            new Player("Mike Mussina", "NYY", Position.Catcher, 74, 195, 35.0),
            new Player("Mike Myers", "NYY", Position.Catcher, 75, 205, 34.0),
            new Player("Mariano Rivera", "NYY", Position.Catcher, 74, 190, 33.0),
            new Player("Jason Giambi", "NYY", Position.Catcher, 75, 220, 32.0),
            new Player("Andy Pettitte", "NYY", Position.Catcher, 77, 210, 31.0)
        );
        when(resolver.calculate5HighestPlayer()).thenReturn(topPlayers);
        presenter.highestPlayers(12345L);
        String expectedMessage = "\n" +
                "T.J. Beam - height 79 inches. Team: NYY\n" +
        "Humberto Sanchez - height 78 inches. Team: NYY\n" +
        "Carl Pavano - height 77 inches. Team: NYY\n" +
        "Andy Pettitte - height 77 inches. Team: NYY\n"+
        "Jose Veras - height 77 inches. Team: NYY";
        verify(view).showTextMessage(eq(12345L), eq(expectedMessage));
    }

    @Test
    void teamWithHighestAverageAge_CallsViewWithCorrectData() {
        String bestTeam = "Команда NYY";
        when(resolver.getTeamWithHighestAverageAge()).thenReturn(bestTeam);
        presenter.teamWithHighestAverageAge(12345L);
        verify(view).showTextMessage(eq(12345L), eq(bestTeam));
    }

    @Test
    void processCSVFile_ProcessingFails_ShowsError() {
        doThrow(new RuntimeException("Download failed")).when(resolver).calculateAverageAge();

        presenter.processCSVFile(12345L, "test.csv", "http://invalid-url/test.csv");

        verify(view).showFileError(eq(12345L));
    }

    @Test
    void processCSVFile_WithNullUrl_ProcessesSuccessfully() {
        presenter.processCSVFile(12345L, "data.csv", null);
        
        verify(view).showFileError(eq(12345L));
    }

    @Test
    void processCSVFile_InvalidFileName_CallsViewWithError() {
        presenter.processCSVFile(12345L, "test.txt", "http://example.com/test.txt");
        verify(view).showFileError(eq(12345L));
    }

    @Test
    void start_CallsViewWithWelcomeMessage() {
        presenter.start(12345L);
        verify(view).showWelcomeMessage(12345L);
    }
}