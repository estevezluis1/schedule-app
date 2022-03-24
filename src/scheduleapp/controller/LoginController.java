package scheduleapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduleapp.Auth;
import scheduleapp.database.Appointments;
import scheduleapp.model.Appointment;
import scheduleapp.utils.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Controller for the login view.
 *
 * In this view form, we can see the zone id
 * and allow users to log in.
 *
 * Resources Locale can be French or English.
 */
public class LoginController {
    @FXML Label languageLabel;
    @FXML TextField usernameTextField;
    @FXML TextField passwordTextField;
    @FXML Button loginButton;

    @FXML Label usernameLabel;
    @FXML Label passwordLabel;

    /**
     * When login view is initialize display the ZoneId of the system
     * and set the language of the login page based on system locale setting.
     */
    @FXML
    public void initialize () {
        languageLabel.setText(String.valueOf(ZoneId.systemDefault()));

        // TODO: debugging only. Remove later.
        usernameTextField.setText("test");
        passwordTextField.setText("test");

        usernameLabel.setText(Resource.getValue("username"));
        passwordLabel.setText(Resource.getValue("password"));
        loginButton.setText(Resource.getValue("login_button"));
    }

    /**
     * Disabling fields prevents any modification while login is in progress.
     *
     * @param disable set disable loginButton, usernameTextField and passwordTextField.
     */
    private void loginDisable (boolean disable) {
        usernameTextField.setDisable(disable);
        passwordTextField.setDisable(disable);
        loginButton.setDisable(disable);
    }


    /**
     * loginButtonClick is invoked with login button is clicked.
     * Here we authenticate username and password.
     * We record login activity then open main form is auth is success.
     *
     * @param event event is used to grab scene from the attached source.
     */
    @FXML
    protected void loginButtonClick (ActionEvent event) {
        loginDisable(true);

        final String username = usernameTextField.getText();
        final String password = passwordTextField.getText();

        try {
            if (Auth.login(username, password)) {
                Log.info("User: " + Auth.getActiveUser().getUsername() + " Logged in successfully");
                ArrayList<Appointment> appointmentArrayList = Appointments.getUpcoming(Auth.getActiveUser().getId());



                String header = Resource.getValue("no_upcoming_appointment");
                String content = "";

                if (appointmentArrayList.size() > 0) {
                    Appointment appointment = appointmentArrayList.get(0);
                    header = Resource.getValue("upcoming_appointment");
                    content =  String.format("ID: %d\tStart Date and Time %s", appointment.getId(), Utilities.localDateTimeFormat(appointment.getStartLocalDateTime()));
                }

                Control.alertDialog(Resource.getValue("upcoming_appointment_title"), header, content, Alert.AlertType.INFORMATION);

                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();

                final int mainViewWidth = 800;
                final int mainViewHeight = 600;
                final String mainTitle = "Schedule Application";

                Utilities.stageSetup(stage, mainTitle, mainViewWidth, mainViewHeight, true, "main-view.fxml");
            } else {
                Log.info("Failed login attempt. Username: " + username);
                Control.alertDialog(
                        Resource.getValue("incorrect_credential_title"),
                        Resource.getValue("incorrect_credential_body"),
                        Resource.getValue("please_try_again"),
                        Alert.AlertType.ERROR
                );
                loginDisable(false);
            }
        } catch (IOException | SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
