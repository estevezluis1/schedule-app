package scheduleapp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.application.Application;
import scheduleapp.utils.Log;
import scheduleapp.utils.Resource;
import scheduleapp.utils.Utilities;
import scheduleapp.database.Database;

/**
 * Main application for Client Scheduling System.
 */
public class ScheduleApp extends Application {
    /**
     * Start Application sets the resource locale and opens database connection.
     * Finally, opens the login page.
     *
     * LAMBDA JUSTIFICATION: gets rid of anonymous inner class for handling WindowEvent when the application exit button is clicked.
     * Hence, shortens code.
     *
     * @param stage stage will be used to for login page.
     * @throws IOException IO exception is thrown if failed to load or initialize fxml
     */
    @Override
    public void start(Stage stage) throws IOException {
        Log.init();
        Resource.setResourceLocale(Locale.getDefault());
        Database.openConnection();

        stage.setOnCloseRequest(event -> {
            try {
                Database.closeConnection();
            } catch (SQLException sqlException) {
                System.out.println(sqlException.getMessage());
            }
            Platform.exit();
        });

        final int loginWidth = 350;
        final int loginHeight = 275;
        final String loginTitle = Resource.getValue("title");

        Utilities.stageSetup(stage, loginTitle, loginWidth, loginHeight, false, "login-view.fxml");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}