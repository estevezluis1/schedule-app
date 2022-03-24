package scheduleapp.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import scheduleapp.Auth;
import scheduleapp.ScheduleApp;
import scheduleapp.database.Appointments;
import scheduleapp.database.Contacts;
import scheduleapp.database.Customers;
import scheduleapp.database.Users;
import scheduleapp.model.Appointment;
import scheduleapp.model.Contact;
import scheduleapp.model.Customer;
import scheduleapp.model.User;
import scheduleapp.utils.Control;
import scheduleapp.utils.Utilities;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.*;
import java.util.*;

/**
 * Dialog window for appointments.
 * @param <ButtonType>
 */
public class AppointmentDialog<ButtonType> extends Dialog<ButtonType> {

    Appointment appointment;
    Appointment updatedAppointment = null;

    TextField idTextField;
    TextField titleTextField;
    TextField typeTextField;
    TextArea descriptionTextArea;
    TextField locationTextField;
    ComboBox<Contact> contactComboBox;
    ComboBox<Customer> customerComboBox;
    ComboBox<User> userComboBox;
    DatePicker datePicker;
    ComboBox<String> startHourComboBox;
    ComboBox<String> startMinuteComboBox;
    ComboBox<String> endHourComboBox;
    ComboBox<String> endMinuteComboBox;

    /**
     *
     * @param saveType save ButtonType to invoke to save appointment.
     * @param cancelType cancel ButtonType to invoke to cancel appointment update.
     * @param appointment appointment to update. if null, then appointment to add.
     */
    public AppointmentDialog (javafx.scene.control.ButtonType saveType, javafx.scene.control.ButtonType cancelType, Appointment appointment) {
        this.appointment = appointment;

        if (appointment == null) {
            final String createdBy = Auth.getActiveUser().getUsername();
            LocalDateTime now = LocalDateTime.now();
            this.appointment = new Appointment(-1, "", "", "", "", -1, -1, -1, createdBy, now, now);
        }

        FXMLLoader loader = new FXMLLoader(ScheduleApp.class.getClassLoader().getResource("appointment-dialog.fxml"));

        try {
            DialogPane dialogPane = loader.load();

            setDialogPane(dialogPane);
            bind(dialogPane);

            getDialogPane().getButtonTypes().addAll(saveType, cancelType);

            Button saveButton = (Button) getDialogPane().lookupButton(saveType);
            saveButton.addEventFilter(ActionEvent.ACTION, this::saveButtonClick);

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     *
     * @param customerId customerId to search.
     * @param customers ArrayList of customers to search for customerId.
     * @return Customer or null, if not found.
     */
    private Customer searchCustomerById (int customerId, ArrayList<Customer> customers) {
        return customers.stream().filter(customer -> customer.getId() == customerId).findAny().orElse(null);
    }
    /**
     *
     * @param userId userId to search.
     * @param users ArrayList of users to search for userId.
     * @return User or null, if not found.
     */
    private User searchUserById (int userId, ArrayList<User> users) {
        return users.stream().filter(user -> user.getId() == userId).findAny().orElse(null);
    }

    /**
     *
     * @param contactId contactId to search.
     * @param contacts ArrayList of contacts to search for contactId.
     * @return Contact or null, if not found.
     */
    private Contact searchContactById(int contactId, ArrayList<Contact> contacts) {
        return contacts.stream().filter(contact -> contact.getId() == contactId).findAny().orElse(null);
    }

    /**
     *
     * @return Appointment that will be inserted or updated.
     */
    public Appointment getAppointment () {
        return updatedAppointment;
    }

    /**
     *
     * @param parent parent Node to search for contents of Appointment Dialog.
     */
    private void bind (Node parent) {
        this.idTextField = (TextField) parent.lookup("#idTextField");
        this.titleTextField = (TextField) parent.lookup("#titleTextField");
        this.typeTextField = (TextField) parent.lookup("#typeTextField");
        this.descriptionTextArea = (TextArea) parent.lookup("#descriptionTextArea");
        this.locationTextField = (TextField) parent.lookup("#locationTextField");
        this.contactComboBox = (ComboBox<Contact>) parent.lookup("#contactComboBox");
        this.customerComboBox = (ComboBox<Customer>) parent.lookup("#customerComboBox");
        this.userComboBox = (ComboBox<User>) parent.lookup("#userComboBox");

        Control.addTextLimiter(titleTextField, 50);
        Control.addTextLimiter(typeTextField, 50);
        Control.addTextLimiter(descriptionTextArea, 50);
        Control.addTextLimiter(locationTextField, 50);


        this.datePicker = (DatePicker) parent.lookup("#datePicker");

        this.startHourComboBox = (ComboBox<String>) parent.lookup("#startHourComboBox");
        this.startMinuteComboBox = (ComboBox<String>) parent.lookup("#startMinuteComboBox");

        this.endHourComboBox = (ComboBox<String>) parent.lookup("#endHourComboBox");
        this.endMinuteComboBox = (ComboBox<String>) parent.lookup("#endMinuteComboBox");

        ObservableList<String> hours = FXCollections.observableArrayList();
        ObservableList<String> minutes = FXCollections.observableArrayList();

        DecimalFormat decimalFormat = new DecimalFormat("00");

        for (int i = 0; i < 60; i++) {
            minutes.add(decimalFormat.format(i));
        }

        for (int i = 0; i < 24; i++) {
            hours.add(decimalFormat.format(i));
        }

        this.startHourComboBox.setItems(hours);
        this.endHourComboBox.setItems(hours);

        this.startMinuteComboBox.setItems(minutes);
        this.endMinuteComboBox.setItems(minutes);

        Control.setCellFactoryComboBox(contactComboBox);
        Control.setCellFactoryComboBox(customerComboBox);
        Control.setCellFactoryComboBox(userComboBox);

        ArrayList<Contact> contactArrayList = new ArrayList<>();
        ArrayList<Customer> customerArrayList = new ArrayList<>();
        ArrayList<User> userArrayList = new ArrayList<>();

        try {
            contactArrayList.addAll(Contacts.getAll());
            customerArrayList.addAll(Customers.getAll());
            userArrayList.addAll(Users.getAll());

            contactComboBox.setItems(FXCollections.observableArrayList(contactArrayList));
            customerComboBox.setItems(FXCollections.observableArrayList(customerArrayList));
            userComboBox.setItems(FXCollections.observableArrayList(userArrayList));
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }

        idTextField.setText(this.appointment.getId() > -1 ? String.valueOf(this.appointment.getId()) : "AUTO GEN");
        titleTextField.setText(this.appointment.getTitle());
        descriptionTextArea.setText(this.appointment.getDescription());
        locationTextField.setText(this.appointment.getLocation());
        typeTextField.setText(this.appointment.getType());

        LocalDateTime start = this.appointment.getStartLocalDateTime();
        LocalDateTime end = this.appointment.getEndLocalDateTime();

        datePicker.setValue(start.toLocalDate());
        startHourComboBox.setValue(decimalFormat.format(start.getHour()));
        startMinuteComboBox.setValue(decimalFormat.format(start.getMinute()));

        endHourComboBox.setValue(decimalFormat.format(end.getHour()));
        endMinuteComboBox.setValue(decimalFormat.format(end.getMinute()));

        Contact selectedContact = this.searchContactById(this.appointment.getContactId(), contactArrayList);
        Customer selectedCustomer = this.searchCustomerById(this.appointment.getCustomerId(), customerArrayList);
        User selectedUser = this.searchUserById(this.appointment.getUserId(), userArrayList);

        contactComboBox.setValue(selectedContact);
        customerComboBox.setValue(selectedCustomer);
        userComboBox.setValue(selectedUser);
    }

    /**
     *
     * @param event event to consume if Appointment is not valid to save.
     */
    private void saveButtonClick (ActionEvent event) {
        final String title = titleTextField.getText();
        final String type = typeTextField.getText();
        final String description = descriptionTextArea.getText();
        final String location = locationTextField.getText();
        final String createdBy = this.appointment.getCreatedBy();

        if (customerComboBox.getSelectionModel().isEmpty()) {
            Control.alertDialog("Customer", "Please select customer", "", Alert.AlertType.ERROR);
            event.consume();
            return;
        }
        if (userComboBox.getSelectionModel().isEmpty()) {
            Control.alertDialog("User", "Please select user", "", Alert.AlertType.ERROR);
            event.consume();
            return;
        }
        if (contactComboBox.getSelectionModel().isEmpty()) {
            Control.alertDialog("Contact", "Please select contact", "", Alert.AlertType.ERROR);
            event.consume();
            return;
        }

        final int contactId = contactComboBox.getValue().getId();
        final int customerId = customerComboBox.getValue().getId();
        final int userId = userComboBox.getValue().getId();

        final LocalDate date = datePicker.getValue();
        final String startHour = startHourComboBox.getValue();
        final String startMinute = startMinuteComboBox.getValue();

        final String endHour = endHourComboBox.getValue();
        final String endMinute = endMinuteComboBox.getValue();

        LocalDateTime inputStart = LocalDateTime.of(
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                Integer.parseInt(startHour), Integer.parseInt(startMinute)
        );
        LocalDateTime inputEnd = LocalDateTime.of(
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                Integer.parseInt(endHour), Integer.parseInt(endMinute)
        );

        ZonedDateTime startDateTime = inputStart.atZone(ZoneId.systemDefault());
        ZonedDateTime endDateTime = inputEnd.atZone(ZoneId.systemDefault());

        ZoneId officeZoneId = ZoneId.of("America/New_York");

        ZonedDateTime startEST = startDateTime.withZoneSameInstant(officeZoneId);
        ZonedDateTime endEST = endDateTime.withZoneSameInstant(officeZoneId);

        if (inputStart.isAfter(inputEnd)) {
            Control.alertDialog("Time", "End time must be after start time.", "Please adjust time.", Alert.AlertType.ERROR);
            event.consume();
            return;
        }

        if (Utilities.notWithinBusinessHours(startEST) || Utilities.notWithinBusinessHours(endEST)) {
            Control.alertDialog("Not within EST Business Hours", "Start and/or end time is not within business hours.", "Please try again", Alert.AlertType.ERROR);
            event.consume();
            return;
        }

        try {
            Appointment existingAppointmentInSlot = Appointments.getAppointmentInSlot(startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime(), appointment.getId());
            if (existingAppointmentInSlot != null) {
                Control.alertDialog("Appointment Overlap", "Appointment time overlaps with existing appointment.", String.format("Overlaps with Appointment ID: %d\nPlease choose a different time slot.", existingAppointmentInSlot.getId()), Alert.AlertType.ERROR);
                event.consume();
                return;
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }

        updatedAppointment = new Appointment(
                this.appointment.getId(), title, description, location, type,
                customerId, userId, contactId, createdBy, startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime()
        );
    }
}
