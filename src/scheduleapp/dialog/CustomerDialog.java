package scheduleapp.dialog;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import scheduleapp.ScheduleApp;
import scheduleapp.database.Countries;
import scheduleapp.database.FirstLevels;
import scheduleapp.model.Customer;
import scheduleapp.model.division.Country;
import scheduleapp.model.division.Division;
import scheduleapp.model.division.FirstLevel;
import scheduleapp.utils.Control;
import scheduleapp.utils.Utilities;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Dialog window for Customers.
 * @param <ButtonType>
 */
public class CustomerDialog<ButtonType> extends Dialog<ButtonType> {

    Customer customer;
    Customer updatedCustomer = null;

    TextField customerIdTextField;
    TextField nameTextField;
    TextField addressTextField;
    TextField postalCodeTextField;
    TextField phoneTextField;

    ComboBox<Country> countryComboBox;
    ComboBox<FirstLevel> firstLevelComboBox;

    HashMap<Integer, ArrayList<FirstLevel>> firstLevelHashMap = new HashMap<>();

    /**
     *
     * @param saveType save ButtonType to invoke to save customer.
     * @param cancelType cancel ButtonType to invoke to cancel customer update.
     * @param customer customer to update. if null, then customer to add.
     */
    public CustomerDialog(javafx.scene.control.ButtonType saveType, javafx.scene.control.ButtonType cancelType, Customer customer) {
        this.customer = customer;

        if (customer == null) {
            this.customer = new Customer(-1, "", "", "", "", -1, "", -1, "");
        }

        FXMLLoader loader = new FXMLLoader(ScheduleApp.class.getClassLoader().getResource("customer-dialog.fxml"));

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
     * @return Customer that will be inserted or updated.
     */
    public Customer getCustomer () {
        return updatedCustomer;
    }

    /**
     *
     * @param divisionId divisionId to search.
     * @param divisions ArrayList of divisions to search for divisionId.
     * @return Division or null, if not found.
     */
    private Division searchDivisionById (int divisionId, ArrayList<? extends Division> divisions) {
        return divisions.stream().filter(division -> division.getId() == divisionId).findAny().orElse(null);
    }

    /**
     *
     * @param parent parent Node to search for contents of Appointment Dialog.
     */
    private void bind (Node parent) {
        this.customerIdTextField = (TextField) parent.lookup("#customerIdTextField");
        this.nameTextField = (TextField) parent.lookup("#nameTextField");
        this.addressTextField = (TextField) parent.lookup("#addressTextField");
        this.postalCodeTextField = (TextField) parent.lookup("#postalCodeTextField");
        this.phoneTextField = (TextField) parent.lookup("#phoneTextField");

        this.countryComboBox = (ComboBox<Country>) parent.lookup("#countryComboBox");
        this.firstLevelComboBox = (ComboBox<FirstLevel>) parent.lookup("#firstLevelComboBox");

        this.nameTextField.setText(this.customer.getName());
        this.addressTextField.setText(this.customer.getAddress());
        this.postalCodeTextField.setText(this.customer.getPostalCode());
        this.phoneTextField.setText(this.customer.getPhone());

        Control.setCellFactoryComboBox(countryComboBox);
        Control.setCellFactoryComboBox(firstLevelComboBox);

        try {
            ArrayList<Country> countryArrayList = Countries.getAll();
            Country country = (Country) searchDivisionById(this.customer.getCountryId(), countryArrayList);

            countryComboBox.setItems(FXCollections.observableArrayList(countryArrayList));
            countryComboBox.setValue(country);

            ArrayList<FirstLevel> firstLevelArrayList = FirstLevels.getAllByCountryId(this.customer.getCountryId());
            FirstLevel firstLevel = (FirstLevel) searchDivisionById(this.customer.getDivisionId(), firstLevelArrayList);

            this.firstLevelHashMap.put(this.customer.getCountryId(), firstLevelArrayList);

            firstLevelComboBox.setItems(FXCollections.observableArrayList(firstLevelArrayList));
            firstLevelComboBox.setValue(firstLevel);

        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }

        countryComboBox.getSelectionModel().selectedItemProperty().addListener((_ov, _previouslySelected, selected) -> {
            final int countryId = selected.getId();

            if (this.firstLevelHashMap.containsKey(countryId)) {
                ArrayList<FirstLevel> firstLevelArrayList = this.firstLevelHashMap.get(countryId);

                firstLevelComboBox.getItems().clear();
                firstLevelComboBox.setItems(FXCollections.observableArrayList(firstLevelArrayList));
                return;
            }

            try {
                ArrayList<FirstLevel> firstLevelArrayList = FirstLevels.getAllByCountryId(selected.getId());

                this.firstLevelHashMap.put(selected.getId(), firstLevelArrayList);
                firstLevelComboBox.getItems().clear();
                firstLevelComboBox.setItems(FXCollections.observableArrayList(firstLevelArrayList));
            } catch (SQLException sqlException) {
                System.out.println(sqlException.getMessage());
            }
        });

        this.customerIdTextField.setText(this.customer.getId() > -1 ? String.valueOf(this.customer.getId()) : "AUTO GEN");
    }

    /**
     *
     * @param event event to consume if Customer is not valid to save.
     */
    private void saveButtonClick (ActionEvent event) {
        if (countryComboBox.getSelectionModel().isEmpty()) {
            Control.alertDialog("Country", "Please select a country.", "", Alert.AlertType.ERROR);
            event.consume();
            return;
        }

        if (firstLevelComboBox.getSelectionModel().isEmpty()) {
            Control.alertDialog("City/Province", "Please select a city/province.", "", Alert.AlertType.ERROR);
            event.consume();
            return;
        }

        int id = Math.max(this.customer.getId(), -1);

        String name = this.nameTextField.getText();
        String address = this.addressTextField.getText();
        String postalCode = this.postalCodeTextField.getText();
        String phone = this.phoneTextField.getText();

        int firstLevelId = this.firstLevelComboBox.getValue().getId();
        String firstLevel = this.firstLevelComboBox.getValue().getName();

        int countryId = this.countryComboBox.getValue().getId();
        String country = this.countryComboBox.getValue().getName();

        if (updatedCustomer == null) {
            updatedCustomer = new Customer(id, name, address, postalCode, phone, firstLevelId, firstLevel, countryId, country);
        }
    }
}
