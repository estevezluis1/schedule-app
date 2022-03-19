package scheduleapp.utils;

import javafx.scene.control.*;
import javafx.util.Callback;
import scheduleapp.model.Contact;
import scheduleapp.model.Customer;
import scheduleapp.model.MonthCountType;
import scheduleapp.model.User;
import scheduleapp.model.division.Division;

import java.util.Optional;

/**
 * Control classes handles unique control functions.
 */
public class Control {

    /**
     *
     * @param textInputControl Control to prevent text more than max length.
     * @param maxLength set the maxlength.
     * @param <T> any control that allows text input.
     */
    public static <T extends TextInputControl> void addTextLimiter(T textInputControl, int maxLength) {
        textInputControl.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textInputControl.getText().length() > maxLength) {
                String s = textInputControl.getText().substring(0, maxLength);
                textInputControl.setText(s);
            }
        });
    }

    /**
     *
     * @param comboBox combobox to set String formatting.
     * @param <T> Type accepted is FirstLevel, Country, Customer, Contact, and User.
     */
    public static <T> void setCellFactoryComboBox (ComboBox<T> comboBox) {
        Callback<ListView<T>, ListCell<T>> cellFactory = new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> _listView) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {

                            if (item instanceof Division) {
                                final Division division = (Division) item;
                                this.setText( division.getName());
                            } else if (item instanceof Contact) {
                                final Contact contact = (Contact) item;

                                final String text = String.format("ID: %d | %s | %s",
                                        contact.getId(), contact.getName(), contact.getEmail());

                                this.setText(text);
                            } else if (item instanceof Customer) {
                                final Customer customer = (Customer) item;

                                final String text = String.format("ID: %d | %s", customer.getId(), customer.getName());

                                this.setText(text);
                            } else if (item instanceof User) {
                                User user = (User) item;

                                final String text = String.format("ID: %d | %s", user.getId(), user.getUsername());

                                this.setText(text);
                            } else if (item instanceof MonthCountType) {
                                this.setText(((MonthCountType) item).getMonth());
                            }
                        }
                    }
                };
            }
        };

        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
    }

    /**
     * alertDialog adds content to alertDialog of any AlertType.
     * @param title title of alert.
     * @param headerText header of alert.
     * @param contentText description of alert.
     * @param alertType type of alert.
     */
    public static void alertDialog(String title, String headerText, String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);

        DialogPane dialogPane = alert.getDialogPane();

        alert.setDialogPane(dialogPane);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    /**
     * alertDialog adds content to alertDialog of any AlertType.
     * @param title title of alert.
     * @param headerText header of alert.
     * @param contentText description of alert.
     * @param alertType type of alert.
     * @return true if ok button is clicked.
     */
    public static boolean confirmationDialog(String title, String headerText, String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);

        DialogPane dialogPane = alert.getDialogPane();

        alert.setDialogPane(dialogPane);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && ButtonType.OK == result.get();
    }
}