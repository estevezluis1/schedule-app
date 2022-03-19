package scheduleapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduleapp.database.Appointments;
import scheduleapp.database.Contacts;
import scheduleapp.database.Customers;
import scheduleapp.model.Appointment;
import scheduleapp.model.Contact;
import scheduleapp.model.Customer;
import scheduleapp.model.MonthCountType;
import scheduleapp.utils.Control;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Controller for report view.
 *
 * In this view we can view 3 different reports.
 *
 */
public class ReportController {

    @FXML private ComboBox<MonthCountType> monthComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label monthTypeLabel;

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private Label customerLabel;

    @FXML private ComboBox<Contact> contactComboBox;
    @FXML private TableView<Appointment> contactTableView;

    // contactTableView TableColumns
    @FXML private TableColumn<Integer, Appointment> aIdColumn;
    @FXML private TableColumn<String, Appointment> aLocationColumn;
    @FXML private TableColumn<String, Appointment> aTypeColumn;
    @FXML private TableColumn<Integer, Appointment> aCustomerIdColumn;
    @FXML private TableColumn<String, Appointment> aTitleColumn;
    @FXML private TableColumn<String, Appointment> aDescColumn;
    @FXML private TableColumn<Integer, Appointment> aContactColumn;
    @FXML private TableColumn<LocalDateTime, Appointment> aStartColumn;
    @FXML private TableColumn<LocalDateTime, Appointment> aEndColumn;
    @FXML private TableColumn<Integer, Appointment> aUserIdColumn;

    @FXML
    protected void initialize () {
        aIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        aTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        aDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        aLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        aContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        aTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        aStartColumn.setCellValueFactory(new PropertyValueFactory<>("startLocalDateTime"));
        aEndColumn.setCellValueFactory(new PropertyValueFactory<>("endLocalDateTime"));
        aUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        aCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        Control.setCellFactoryComboBox(customerComboBox);
        Control.setCellFactoryComboBox(contactComboBox);
        Control.setCellFactoryComboBox(monthComboBox);

        typeComboBox.getSelectionModel().selectedItemProperty().addListener((_observable, _oldValue, newValue) -> {
            typeComboBox.setDisable(true);
            monthComboBox.setDisable(true);

            monthComboBox.getItems().clear();

            try {
                ArrayList<MonthCountType> monthCountTypes = Appointments.getMonthByType(newValue);
                monthComboBox.setItems(FXCollections.observableArrayList(monthCountTypes));
            } catch (SQLException sqlException) {
                System.out.println(sqlException.getMessage());
            }

            typeComboBox.setDisable(false);
            monthComboBox.setDisable(false);
        });

        monthComboBox.getSelectionModel().selectedItemProperty().addListener((_observable, _oldValue, newValue) -> {
            if (monthComboBox.getSelectionModel().isEmpty()) {
                return;
            }

            MonthCountType monthCount = monthComboBox.getSelectionModel().getSelectedItem();

            monthTypeLabel.setText(
                monthCount.getMonthCount() + " appointments with TYPE: \"" +
                        monthCount.getType() + "\" in MONTH: \"" + monthCount.getMonth()
            );
        });

        contactComboBox.getSelectionModel().selectedItemProperty().addListener((_observable, _oldValue, newValue) -> {
            contactComboBox.setDisable(true);
            contactTableView.getItems().clear();

            try {
                ArrayList<Appointment> appointmentArrayList = Appointments.getAllById(newValue.getId(), "Contact_ID");
                contactTableView.setItems(FXCollections.observableArrayList(appointmentArrayList));
            } catch (SQLException sqlException) {
                System.out.println(sqlException.getMessage());
            }

            contactComboBox.setDisable(false);
        });

        customerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int count = 0;
            try {
                count = Appointments.getAllById(newValue.getId(), "Customer_ID").size();
            } catch (SQLException sqlException) {
                System.out.println(sqlException.getMessage());
            }

            customerLabel.setText("Customer has: " + count + " appointments in the past and/or future.");
        });

        try {
            ArrayList<Contact> contacts = Contacts.getAll();
            ArrayList<String> types = Appointments.getTypes();
            ArrayList<Customer> customers = Customers.getAll();

            // get customers not in the United States
            customers.removeIf(customer -> customer.getCountryId() == 1);

            typeComboBox.setItems(FXCollections.observableArrayList(types));
            contactComboBox.setItems(FXCollections.observableArrayList(contacts));
            customerComboBox.setItems(FXCollections.observableArrayList(customers));
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }
}
