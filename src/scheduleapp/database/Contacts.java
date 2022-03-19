package scheduleapp.database;

import scheduleapp.model.Contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static class for querying contacts from db.
 */
public class Contacts {
    private static final String table = "contacts";

    /**
     *
     * @return table name "contacts"
     */
    public static String getTable () {
        return table;
    }

    /**
     * Get all contacts.
     *
     * @return ArrayList of Contact
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Contact> getAll () throws SQLException {
        ArrayList<Contact> contactsArrayList = new ArrayList<>();

        final List<String> columns = Arrays.asList(getTable() + ".Contact_ID", getTable() + ".Contact_Name", getTable() + ".Email");

        String query = Database.select(getTable(), columns, new String[0], new String[0]);

        ResultSet resultSet = Database.getPreparedStatement(query).executeQuery();

        while (resultSet.next()) {
            final int id = resultSet.getInt("Contact_ID");
            final String name = resultSet.getString("Contact_Name");
            final String email = resultSet.getString("Email");

            Contact contact = new Contact(id, name, email);
            contactsArrayList.add(contact);
        }

        return contactsArrayList;
    }
}
