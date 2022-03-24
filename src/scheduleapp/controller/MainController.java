package scheduleapp.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduleapp.Auth;
import scheduleapp.dialog.AppointmentDialog;
import scheduleapp.dialog.CustomerDialog;
import scheduleapp.database.Appointments;
import scheduleapp.database.Customers;
import scheduleapp.model.Appointment;
import scheduleapp.model.Customer;
import scheduleapp.utils.Control;
import scheduleapp.utils.Resource;
import scheduleapp.utils.Utilities;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller for main view.
 *
 * In this view we can manage appointments and customers.
 *
 */
public class MainController {
    @FXML
    private MenuItem activeUser;

    @FXML private ToggleGroup filterBy;
    @FXML private RadioButton monthRadioButton;
    @FXML private RadioButton weekRadioButton;
    @FXML private RadioButton allRadioButton;

    // TableView
    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableView<Customer> customerTableView;

    // appointmentTableView TableColumns
    @FXML private TableColumn<Appointment, Integer> aIdColumn;
    @FXML private TableColumn<Appointment, String> aLocationColumn;
    @FXML private TableColumn<Appointment, String> aTypeColumn;
    @FXML private TableColumn<Appointment, Integer> aCustomerIdColumn;
    @FXML private TableColumn<Appointment, String> aTitleColumn;
    @FXML private TableColumn<Appointment, String> aDescColumn;
    @FXML private TableColumn<Appointment, Integer> aContactColumn;
    @FXML private TableColumn<Appointment, String> aStartColumn;
    @FXML private TableColumn<Appointment, String> aEndColumn;
    @FXML private TableColumn<Appointment, Integer> aUserIdColumn;

    // customerTableView TableColumns
    @FXML private TableColumn<Integer, Customer> cIdColumn;
    @FXML private TableColumn<String, Customer> cNameColumn;
    @FXML private TableColumn<String, Customer> cAddressColumn;
    @FXML private TableColumn<String, Customer> cPostalCodeColumn;
    @FXML private TableColumn<String, Customer> cPhoneColumn;
    @FXML private TableColumn<String, Customer> cDivisionColumn;
    @FXML private TableColumn<String, Customer> cCountryColumn;

    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button modifyAppointmentButton;
    @FXML
    private Button deleteAppointmentButton;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button modifyCustomerButton;
    @FXML
    private Button deleteCustomerButton;

    @FXML
    private MenuItem logOutMenuItem;

    @FXML
    private Menu accountMenu;

    private final ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    private final ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    /**
     * fetch and set appointments.
     */
    private void fetchAndSetAppointments () {
        try {
            sortAndSet(Appointments.getAll());
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    /**
     * fetch and set appointments by start and end range.
     * @param start Start range
     * @param end End range
     */
    private void fetchAndSetAppointments (LocalDateTime start, LocalDateTime end) {
        try {
            sortAndSet(Appointments.getByRange(start, end));
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    /**
     * sort ascending order and set to table view.
     *
     * LAMBDA JUSTIFICATION: gets rid of anonymous inner class for the Comparator, when sorting appointments in ascending order.
     * Hence, shorting code.
     *
     * @param appointmentArrayList arraylist of appointments to sort.
     */
    private void sortAndSet (ArrayList<Appointment> appointmentArrayList) {
        appointmentArrayList.sort((a1, a2) -> a1.getId() - a2.getId());
        appointmentTableView.setItems(FXCollections.observableArrayList(appointmentArrayList));
    }

    private void fetchAndSetCustomers () {
        try {
            ArrayList<Customer> customerArrayList = Customers.getAll();
            customerArrayList.sort(Comparator.comparingInt(Customer::getId));
            customerTableView.setItems(FXCollections.observableArrayList(customerArrayList));
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    @FXML
    protected void initialize () {
        this.activeUser.setText(String.format("User ID: %d", Auth.getActiveUser().getId()));

        // appointment cell Value Factory
        aIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        aTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        aDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        aLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        aContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        aTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        aStartColumn.setCellValueFactory(appointmentStringCellDataFeatures -> new SimpleStringProperty(
                Utilities.localDateTimeFormat(appointmentStringCellDataFeatures.getValue().getStartLocalDateTime()))
        );
        aEndColumn.setCellValueFactory(appointmentStringCellDataFeatures -> new SimpleStringProperty(
                Utilities.localDateTimeFormat(appointmentStringCellDataFeatures.getValue().getEndLocalDateTime()))
        );
        aUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        aCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        // Customer cell Value Factory
        cIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));
        cCountryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

        fetchAndSetAppointments();
        fetchAndSetCustomers();

        filterBy.selectedToggleProperty().addListener(this::radioBtnChanged);
    }

    private void radioBtnChanged(ObservableValue<? extends Toggle> _observable, Toggle _old, Toggle _new) {
        appointmentTableView.getItems().clear();


        if (allRadioButton.isSelected()) {
            fetchAndSetAppointments();
            return;
        }

        TemporalField localeWeek = WeekFields.of(Locale.getDefault()).dayOfWeek();

        LocalDate localDate = LocalDate.now();

        LocalDateTime start;
        LocalDateTime end;

        if (monthRadioButton.isSelected()) {
            LocalDateTime firstDayOfMonth = localDate.withDayOfMonth(1).atStartOfDay();
            LocalDateTime lastDayOfMonth = localDate.withDayOfMonth(localDate.lengthOfMonth()).atTime(23, 59, 59);

            start = firstDayOfMonth;
            end = lastDayOfMonth;
        } else {
            int daysToFirstDayOfWeek = localDate.get(localeWeek) - 1;
            int daysToLastDayOfWeek = 7 - daysToFirstDayOfWeek - 1;

            LocalDateTime firstDayOfWeek = localDate.minusDays(daysToFirstDayOfWeek).atTime(0, 0, 0);
            LocalDateTime lastDayOfWeek = localDate.plusDays(daysToLastDayOfWeek).atTime(23, 59, 59);

            start = firstDayOfWeek;
            end = lastDayOfWeek;

            System.out.println(start + " " + end);
        }
        fetchAndSetAppointments(start, end);
    }

    @FXML
    protected void logOutButtonClick() {
        Auth.logOut();

        try {
            final int loginWidth = 350;
            final int loginHeight = 275;
            final String loginTitle = Resource.getValue("title");

            Stage stage = (Stage) customerTableView.getScene().getWindow();

            Utilities.stageSetup(stage, loginTitle, loginWidth, loginHeight, false, "login-view.fxml");
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    private <T> void update (TableView<T> tableView, T item) {
        final int index = tableView.getSelectionModel().getSelectedIndex();

        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());

        tableView.getItems().add(index, item);

        tableView.getSelectionModel().select(index);
    }

    @FXML
    public void addCustomerClick() {
        openCustomerDialog(null);
    }

    @FXML
    public void generateReportsClick () {
        Stage reportWindow = new Stage();

        reportWindow.initModality(Modality.APPLICATION_MODAL);

        final String title = "Reports";

        try {
            Utilities.stageSetup(reportWindow, title, 800, 400, true, "reports-view.fxml");

            reportWindow.showAndWait();
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }
    @FXML
    public void modifyCustomerClick() {
        TableView.TableViewSelectionModel<Customer> selected = this.customerTableView.getSelectionModel();
        if (selected.isEmpty()) {
            Control.alertDialog("Modify Error", "", "Select item before clicking \"Modify\".", Alert.AlertType.INFORMATION);
            return;
        }

        Customer customer = this.customerTableView.getSelectionModel().getSelectedItem();

        openCustomerDialog(customer);
    }

    @FXML
    public void deleteCustomerClick() {
        TableView.TableViewSelectionModel<Customer> selected = this.customerTableView.getSelectionModel();
        if (selected.isEmpty()) {
            Control.alertDialog("Delete Error", "", "Select item before clicking \"Delete\".", Alert.AlertType.INFORMATION);
            return;
        }

        Customer customer = selected.getSelectedItem();

        int numOfAppointments = 0;
        try {
            numOfAppointments = Appointments.getAllById(customer.getId(), "Customer_ID").size();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }

        if (!Control.confirmationDialog(
                "Delete confirmation",
                "Are you sure?",
                String.format("%s will be deleted along with all past and upcoming (%d) appointments.", customer.getName(), numOfAppointments),
                Alert.AlertType.CONFIRMATION
        )) {
            return;
        }

        Control.alertDialog(
                "Customer Deleted",
                "You've successfully deleted the following customer:",
                String.format("ID: %d\tName: %s | Deleted %d attached appointments", customer.getId(), customer.getName(), numOfAppointments),
                Alert.AlertType.INFORMATION
        );

        try {
            Customers.delete(selected.getSelectedItem());
            this.customerTableView.getItems().remove(selected.getSelectedIndex());
            this.customerTableView.refresh();

            this.fetchAndSetAppointments();

            this.appointmentTableView.refresh();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
            Control.alertDialog("Delete Error", "", "Failed to delete from database. If issue persist, please contact", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    public void addAppointmentClick () {
        openAppointmentDialog(null);
    }

    @FXML
    public void modifyAppointmentClick() {
        TableView.TableViewSelectionModel<Appointment> selected = this.appointmentTableView.getSelectionModel();
        if (selected.isEmpty()) {
            Control.alertDialog("Modify Error", "", "Select item before clicking \"Modify\".", Alert.AlertType.INFORMATION);
            return;
        }

        Appointment appointment = this.appointmentTableView.getSelectionModel().getSelectedItem();

        openAppointmentDialog(appointment);

        this.appointmentTableView.refresh();
    }

    @FXML
    public void deleteAppointmentClick() {
        TableView.TableViewSelectionModel<Appointment> selected = this.appointmentTableView.getSelectionModel();
        if (selected.isEmpty()) {
            Control.alertDialog("Delete Error", "", "Select item before clicking \"Delete\".", Alert.AlertType.INFORMATION);
            return;
        }

        Appointment appointment = selected.getSelectedItem();

        if (!Control.confirmationDialog("Delete confirmation", String.format("Delete Appointment with ID: %d\tType: %s", appointment.getId(), appointment.getType()), "Are you sure?", Alert.AlertType.CONFIRMATION)) {
            return;
        }

        Control.alertDialog(
                "Appointment Deleted",
                "You've successfully deleted the following appointment:",
                String.format("ID: %d\tType: %s\tStart Date Time: %s", appointment.getId(), appointment.getType(), appointment.getStartLocalDateTime()),
                Alert.AlertType.INFORMATION
        );

        try {
            Appointments.delete(selected.getSelectedItem());
            this.appointmentTableView.getItems().remove(selected.getSelectedIndex());
            this.appointmentTableView.refresh();

        } catch (SQLException sqlException) {
            Control.alertDialog("Delete Error", "", "Failed to delete from database. If issue persist, please contact", Alert.AlertType.ERROR);
        }
    }

    private void openCustomerDialog (Customer customer) {
        CustomerDialog<ButtonType> customerDialog = new CustomerDialog<>(saveButton, cancelButton, customer);

        Optional<ButtonType> result = customerDialog.showAndWait();

        if (result.isPresent() && result.get() != saveButton) {
            return;
        }

        final int id = customerDialog.getCustomer().getId();

        try {
            if (id == -1) {
                final int insertId = Customers.insert(customerDialog.getCustomer(), Auth.getActiveUser().getUsername());

                Customer newCustomer = Customers.getById(insertId);

                this.customerTableView.getItems().add(newCustomer);
                return;
            }

            Customers.update(customerDialog.getCustomer(), Auth.getActiveUser().getUsername());
            Customer updatedCustomer = Customers.getById(id);
            update(customerTableView, updatedCustomer);

        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    private void openAppointmentDialog (Appointment appointment) {
        AppointmentDialog<ButtonType> appointmentDialog = new AppointmentDialog<>(saveButton, cancelButton, appointment);

        Optional<ButtonType> result = appointmentDialog.showAndWait();

        if (result.isPresent() && result.get() != saveButton) {
            return;
        }

        final int id = appointmentDialog.getAppointment().getId();

        try {
            if (id == -1) {
                final int insertId = Appointments.insert(appointmentDialog.getAppointment());

                Appointment newAppointment = Appointments.getByAppointmentId(insertId);

                this.appointmentTableView.getItems().add(newAppointment);
                return;
            }

            Appointments.update(appointmentDialog.getAppointment(), Auth.getActiveUser().getUsername());
            Appointment updatedAppointment = Appointments.getByAppointmentId(id);
            update(appointmentTableView, updatedAppointment);

        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }
}
