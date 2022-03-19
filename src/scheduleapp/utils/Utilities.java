package scheduleapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduleapp.ScheduleApp;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;

/**
 * Utilities for handling stages and timezones.
 */
public class Utilities {
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
}
