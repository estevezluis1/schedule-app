package scheduleapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduleapp.ScheduleApp;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Utilities for handling stages and timezones.
 */
public class Utilities {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    /**
     * Setup stage for any form.
     * @param stage stage
     * @param stageTitle stage title
     * @param width window width.
     * @param height window height.
     * @param resizable resizable window.
     * @param sceneBuilderLocation location of fxml scene to build.
     * @throws IOException if trouble with fxml.load()
     */
    public static void stageSetup(Stage stage, String stageTitle, double width, double height, boolean resizable, String sceneBuilderLocation) throws IOException {
        stage.setTitle(stageTitle);
        stage.setResizable(resizable);

        final URL sceneBuilder = ScheduleApp.class.getClassLoader().getResource(sceneBuilderLocation);

        FXMLLoader fxmlLoader = new FXMLLoader(sceneBuilder);

        Scene windowForm = new Scene(fxmlLoader.load(), width, height);

        stage.setScene(windowForm);
        stage.centerOnScreen();
    }
    /**
     *
     * @param zonedDateTime DateTime that should be in EST zone.
     * @return true if zonedDateTime is not within business hours and days. 8AM - 10PM EST
     */
    public static boolean notWithinBusinessHours (ZonedDateTime zonedDateTime) {
        return (zonedDateTime.getHour() < 8 || zonedDateTime.getHour() > 22 || (zonedDateTime.getHour() == 22 && zonedDateTime.getMinute() > 0));
    }

    /**
     * Format local date time for easy reading.
     * @param localDateTime localDateTime to format.
     * @return formatted localDateTime.
     */
    public static String localDateTimeFormat (LocalDateTime localDateTime) {
        return localDateTime.format(dateTimeFormatter);
    }
}
