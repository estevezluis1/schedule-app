package scheduleapp;

import scheduleapp.database.Users;
import scheduleapp.model.User;

import java.sql.SQLException;

/**
 * Auth class holds login user in memory.
 */
public class Auth {
    private static User activeUser = null;

    /**
     *
     * @param username username to match against database.
     * @param password password to match against database.
     * @return true if username and password is a match in database,
     * @throws SQLException if database failed to execute or process row data.
     */
    public static boolean login (String username, String password) throws SQLException {
        activeUser = Users.getByUsernamePassword(username, password);

        return activeUser != null;
    }

    /**
     *
     * @return active logged in user.
     */
    public static User getActiveUser () {
        return activeUser;
    }

    /**
     * logOut when invoked removed user data from memory.
     */
    public static void logOut () {
        activeUser = null;
    }
}
