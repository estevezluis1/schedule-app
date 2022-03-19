package scheduleapp.database;

import scheduleapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static class for querying users from db.
 */
public class Users {
    private final static String tableName = "users";

    /**
     *
     * @return tableName "users"
     */
    private static String getTable () {
        return tableName;
    }

    /**
     *
     * @param username username of user to match
     * @param password password of user to match
     * @return User if match found, else null.
     * @throws SQLException throws SQLException if error occurs
     */
    public static User getByUsernamePassword (String username, String password) throws SQLException {
        final List<String> columns = Arrays.asList(getTable() + ".User_ID", getTable() + ".User_Name");
        final String[] referenceColumns = {columns.get(1), getTable() + ".Password"};

        String query = Database.select(getTable(), columns, new String[0], referenceColumns);

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // login activity utc
            return new User(resultSet.getInt("User_ID"), resultSet.getString("User_Name"));
        }
        return null;
    }

    /**
     *
     * @return ArrayList of Users
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<User> getAll () throws SQLException {
        ArrayList<User> userArrayList = new ArrayList<>();

        final List<String> columns = Arrays.asList(getTable() + ".User_ID", getTable() + ".User_Name");

        String query = Database.select(getTable(), columns, new String[0], new String[0]);

        ResultSet resultSet = Database.getPreparedStatement(query).executeQuery();

        while (resultSet.next()) {
            final int id = resultSet.getInt("User_ID");
            final String username = resultSet.getString("User_Name");

            User user = new User(id, username);

            userArrayList.add(user);
        }
        return userArrayList;
    }
}
