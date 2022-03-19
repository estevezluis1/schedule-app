package scheduleapp.database;

import scheduleapp.model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static class for querying customers from db.
 */
public class Customers {
    private static final String table = "customers";

    /**
     *
     * @return table name "customers"
     */
    public static String getTable() {
        return table;
    }

    /**
     *
     * @param resultSet ResultSet to parse through
     * @return ArrayList of customers
     * @throws SQLException throws SQLException if error occurs
     */
    private static ArrayList<Customer> parseResultSet (ResultSet resultSet) throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();

        while (resultSet.next()) {
            final int id = resultSet.getInt("Customer_ID");
            final String name = resultSet.getString("Customer_Name");
            final String address = resultSet.getString("Address");
            final String postalCode = resultSet.getString("Postal_Code");
            final String phone = resultSet.getString("Phone");
            final int divisionId = resultSet.getInt("Division_ID");
            final String firstLevel = resultSet.getString("Division");
            final int countryId = resultSet.getInt("Country_ID");
            final String country = resultSet.getString("Country");

            Customer customer = new Customer(id, name, address, postalCode, phone, divisionId, firstLevel, countryId, country);
            customers.add(customer);
        }

        return customers;
    }

    /**
     *
     * @param customerId customer id to search by.
     * @return Customer if found, else null.
     * @throws SQLException throws SQLException if error occurs
     */
    public static Customer getById (int customerId) throws SQLException {
        final List<String> columns = Arrays.asList(
                getTable() + ".Customer_ID", getTable() + ".Customer_Name", getTable() + ".Address",
                getTable() + ".Postal_Code", getTable() + ".Phone", FirstLevels.getTable() + ".Division_ID",
                FirstLevels.getTable() + ".Division", Countries.getTable() + ".Country_ID",
                Countries.getTable() + ".Country"
        );

        final String[] joins = {
                "INNER JOIN " + FirstLevels.getTable() + " ON customers.Division_ID=" + columns.get(5),
                "INNER JOIN " + Countries.getTable() + " ON first_level_divisions.Country_ID=" + columns.get(7)
        };

        final String[] referenceColumns = {getTable() + ".Customer_ID"};

        final String query = Database.select(getTable(), columns, joins, referenceColumns);

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, customerId);

        ArrayList<Customer> customerArrayList = parseResultSet(preparedStatement.executeQuery());

        if (customerArrayList.size() > 0) return customerArrayList.get(0);

        return null;
    }

    /**
     * Get all customers.
     * @return ArrayList of customers.
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Customer> getAll () throws SQLException {
        final List<String> columns = Arrays.asList(
                getTable() + ".Customer_ID", getTable() + ".Customer_Name", getTable() + ".Address",
                getTable() + ".Postal_Code", getTable() + ".Phone", FirstLevels.getTable() + ".Division_ID",
                FirstLevels.getTable() + ".Division", Countries.getTable() + ".Country_ID",
                Countries.getTable() + ".Country"
        );

        final String[] joins = {
                "INNER JOIN " + FirstLevels.getTable() + " ON customers.Division_ID=" + columns.get(5),
                "INNER JOIN " + Countries.getTable() + " ON first_level_divisions.Country_ID=" + columns.get(7)
        };

        final String[] referenceColumns = new String[0];

        final String query = Database.select(getTable(), columns, joins, referenceColumns);

        return parseResultSet(Database.getPreparedStatement(query).executeQuery());
    }

    /**
     *
     * @param command command (UPDATE, INSERT)
     * @param customer Customer to update or insert.
     * @param updatedByUsername username to set in column Last_Updated_By and/nor Created_By
     * @return PreparedStatement
     * @throws SQLException throws SQLException if error occurs
     */
    public static PreparedStatement executeUpdate (Database.COMMAND command, Customer customer, String updatedByUsername) throws SQLException {

        ArrayList<String> columns = new ArrayList<>(Arrays.asList("Customer_Name", "Address", "Postal_Code", "Phone", "Division_ID", "Last_Updated_By"));

        if (command == Database.COMMAND.INSERT) {
            columns.add("Created_By");
        }

        final String query = Database.addTransactionCommit(
                command == Database.COMMAND.INSERT ?
                Database.insert(getTable(), columns) : Database.update(getTable(), columns, "Customer_ID")
        );

        PreparedStatement preparedStatement = command == Database.COMMAND.INSERT ?
                Database.getInsertPreparedStatement(query) : Database.getPreparedStatement(query);

        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getAddress());
        preparedStatement.setString(3, customer.getPostalCode());
        preparedStatement.setString(4, customer.getPhone());
        preparedStatement.setInt(5, customer.getDivisionId());
        preparedStatement.setString(6, updatedByUsername);

        if (command == Database.COMMAND.INSERT) {
            preparedStatement.setString(7, updatedByUsername);
        } else if (command == Database.COMMAND.UPDATE) {
            preparedStatement.setInt(7, customer.getId());
        }

        return preparedStatement;
    }

    /**
     *
     * @param customer customer to update in database.
     * @param updatedByUsername username to set in column Last_Updated_By
     * @throws SQLException throws SQLException if error occurs
     */
    public static void update (Customer customer, String updatedByUsername) throws SQLException {
        executeUpdate(Database.COMMAND.UPDATE, customer, updatedByUsername).executeUpdate();
    }

    /**
     *
     * @param customer customer to insert.
     * @param createdByUsername username to set in column Created_By, Last_Updated_By
     * @return insert id
     * @throws SQLException throws SQLException if error occurs
     */
    public static int insert (Customer customer, String createdByUsername) throws SQLException {
        PreparedStatement preparedStatement = executeUpdate(Database.COMMAND.INSERT, customer, createdByUsername);

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.executeQuery(Database.SELECT_LAST_INSERT_ID);

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }

        return -1;
    }

    /**
     * Appointments must be deleted before customer can be deleted.
     *
     * @param customer customer to delete
     * @throws SQLException throws SQLException if error occurs
     */
    public static void delete (Customer customer) throws SQLException {
        String referenceColumn = "Customer_ID";

        String query = Database.addTransactionCommit(
                Database.delete(Appointments.getTable(), referenceColumn) +
                        Database.delete(getTable(), referenceColumn)
        );

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, customer.getId());
        preparedStatement.setInt(2, customer.getId());

        preparedStatement.execute();
    }
}