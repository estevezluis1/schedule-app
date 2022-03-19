package scheduleapp.database;

import scheduleapp.model.Appointment;
import scheduleapp.model.MonthCountType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static class for querying appointments from db.
 */
public class Appointments {
    private static final String table = "appointments";

    /**
     *
     * @return table name "appointments"
     */
    public static String getTable() {
        return table;
    }

    /**
     *
     * Parse resultSet and convert UTC timestamps to local
     *
     * @param resultSet ResultSet
     * @return ArrayList of Appointments.
     * @throws SQLException throws SQLException if error occurs
     */
    private static ArrayList<Appointment> parseResultSet (ResultSet resultSet) throws SQLException {
        ArrayList<Appointment> appointments = new ArrayList<>();

        while (resultSet.next()) {
            final int appointmentId = resultSet.getInt("Appointment_ID");
            final String title = resultSet.getString("Title");
            final String description = resultSet.getString("Description");
            final String location = resultSet.getString("Location");
            final String type = resultSet.getString("Type");
            final int customerId = resultSet.getInt("Customer_ID");
            final int userId = resultSet.getInt("User_ID");
            final int contactId = resultSet.getInt("contact_ID");

            final String createdBy = resultSet.getString("Created_By");

            LocalDateTime start = resultSet.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = resultSet.getTimestamp("End").toLocalDateTime();

            Appointment appointment = new Appointment(
                    appointmentId, title, description, location, type, customerId,
                    userId, contactId, createdBy, start, end
            );

            appointments.add(appointment);
        }

        return appointments;
    }

    /**
     *
     * @param start LocalDateTime to start search
     * @param end LocalDateTime to end search
     * @return ArrayList of Appointments that matches between start, and end time.
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Appointment> getByRange (LocalDateTime start, LocalDateTime end) throws SQLException {
        final String query = "SELECT DISTINCT * FROM appointments WHERE (Start >= ? AND Start <= ?) OR (End >= ? AND End <= ?);";

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        Timestamp startTimestamp = Timestamp.valueOf(start);
        Timestamp endTimestamp = Timestamp.valueOf(end);

        preparedStatement.setTimestamp(1, startTimestamp);
        preparedStatement.setTimestamp(2, endTimestamp);
        preparedStatement.setTimestamp(3, startTimestamp);
        preparedStatement.setTimestamp(4, endTimestamp);

        return parseResultSet(preparedStatement.executeQuery());
    }

    /**
     *
     * @param start start time to search.
     * @param end end time to search.
     * @param appointmentId appointment id not to check it's self.
     * @return true, if no appointment is within start and end time.
     * @throws SQLException throws SQLException if error occurs
     */
    public static boolean isSlotOpen (LocalDateTime start, LocalDateTime end, int appointmentId) throws SQLException {
        final String query = "SELECT 1 FROM appointments WHERE Start <= ? AND End >= ? AND Appointment_ID <> ?;";

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
        preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
        preparedStatement.setInt(3, appointmentId);

        return !preparedStatement.executeQuery().next();
    }

    /**
     *
     * @param appointmentId appointment to search.
     * @return Appointment with appointment id appointmentId.
     * @throws SQLException throws SQLException if error.
     */
    public static Appointment getByAppointmentId (int appointmentId) throws SQLException {
        return getById(appointmentId, "Appointment_ID");
    }

    /**
     *
     * @param id id to search.
     * @param referenceColumn column of id to search by.
     * @return Appointment that matches referenceColumn value id.
     * @throws SQLException throws SQLException if error occurs
     */
    public static Appointment getById (int id, String referenceColumn) throws SQLException {
        List<String> columns = List.of();
        String[] joins = new String[0];
        String[] referenceColumns = {referenceColumn};

        String query = Database.select(Appointments.getTable(), columns, joins, referenceColumns);

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Appointment> results = parseResultSet(resultSet);

        if (results.size() > 0) {
            return results.get(0);
        }

        return null;
    }

    /**
     *
     * Get All appointments.
     *
     * @return ArrayList of Appointments
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Appointment> getAll () throws SQLException {
        List<String> columns = Arrays.asList(
                "Appointment_ID", "Title", "Description", "Location", "Type", "Customer_ID",
                "User_ID", "Contact_ID", "Created_By", "Start", "End"
        );
        String[] joins = new String[0];
        String[] referenceColumns = new String[0];

        String query = Database.select(Appointments.getTable(), columns, joins, referenceColumns);

        ResultSet resultSet = Database.getPreparedStatement(query).executeQuery();

        return parseResultSet(resultSet);
    }

    /**
     *
     * Get All appointments.
     *
     * @return ArrayList of Appointments
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Appointment> getAllById (int id, String referenceColumn) throws SQLException {
        List<String> columns = Arrays.asList(
                "Appointment_ID", "Title", "Description", "Location", "Type", "Customer_ID",
                "User_ID", "Contact_ID", "Created_By", "Start", "End"
        );
        String[] joins = new String[0];
        String[] referenceColumns = {referenceColumn};

        String query = Database.select(Appointments.getTable(), columns, joins, referenceColumns);

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, id);

        return parseResultSet(preparedStatement.executeQuery());
    }

    /**
     *
     * Search database for appointments within 15 minutes.
     *
     * @param userId userId to search by
     * @return ArrayList of Appointment
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Appointment> getUpcoming (int userId) throws SQLException {
        final String query = "SELECT * FROM appointments WHERE appointments.User_ID = ? AND Start BETWEEN UTC_TIMESTAMP() AND TIMESTAMPADD(MINUTE, 15, UTC_TIMESTAMP());";

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, userId);

        return parseResultSet(preparedStatement.executeQuery());
    }

    /**
     *
     * @param command (UPDATE, INSERT
     * @param appointment appointment to update or insert.
     * @param updatedByUsername username to log in query (Created_By, Last_Updated_By)
     * @return PreparedStatement
     * @throws SQLException throws SQLException if error occurs
     */
    public static PreparedStatement executeUpdate (Database.COMMAND command, Appointment appointment, String updatedByUsername) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        ArrayList<String> columns = new ArrayList<>(Arrays.asList(
                "Title", "Description", "Location", "Type", "Customer_ID",
                "User_ID", "Contact_ID", "start", "end", "Last_Updated_By"
        ));

        if (command == Database.COMMAND.INSERT) {
            columns.add("Created_By");

        } else if (command == Database.COMMAND.UPDATE) {
            queryBuilder.append("UPDATE appointments SET ");
            for (String column : columns) {
                queryBuilder.append(column).append(" = ?,");
            }

            queryBuilder.append("Last_Update = UTC_TIMESTAMP() WHERE Appointment_ID = ?;");
        }

        String query = Database.addTransactionCommit(command == Database.COMMAND.INSERT ?
                 Database.insert(getTable(), columns) : queryBuilder.toString()
        );

        PreparedStatement preparedStatement = command == Database.COMMAND.INSERT ?
                Database.getInsertPreparedStatement(query) : Database.getPreparedStatement(query);

        preparedStatement.setString(1, appointment.getTitle());
        preparedStatement.setString(2, appointment.getDescription());
        preparedStatement.setString(3, appointment.getLocation());
        preparedStatement.setString(4, appointment.getType());
        preparedStatement.setInt(5, appointment.getCustomerId());
        preparedStatement.setInt(6, appointment.getUserId());
        preparedStatement.setInt(7, appointment.getContactId());

        preparedStatement.setTimestamp(8, Timestamp.valueOf(appointment.getStartLocalDateTime()));
        preparedStatement.setTimestamp(9, Timestamp.valueOf(appointment.getEndLocalDateTime()));

        preparedStatement.setString(10, updatedByUsername);

        if (command == Database.COMMAND.INSERT) {
            // Created_By VALUE (?)
            preparedStatement.setString(11, updatedByUsername);
        } else if (command == Database.COMMAND.UPDATE) {
            // WHERE Appointment_ID = ?
            preparedStatement.setInt(11, appointment.getId());
        }

        return preparedStatement;
    }

    /**
     *
     * @param appointment appointment to update.
     * @param updatedByUsername username to log in sql query.
     * @throws SQLException throws SQLException if error occurs
     */
    public static void update (Appointment appointment, String updatedByUsername) throws SQLException {
        executeUpdate(Database.COMMAND.UPDATE, appointment, updatedByUsername).executeUpdate();
    }

    /**
     *
     * @param appointment appointment to be inserted.
     * @return insert id.
     * @throws SQLException throws SQLException if error occurs
     */
    public static int insert (Appointment appointment) throws SQLException {
        PreparedStatement preparedStatement = executeUpdate(
                Database.COMMAND.INSERT, appointment, appointment.getCreatedBy()
        );

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.executeQuery(Database.SELECT_LAST_INSERT_ID);

        if (resultSet.next()) {
            return resultSet.getInt(1);
        }

        return -1;
    }

    /**
     *
     * @param appointment appointment to delete.
     * @throws SQLException throws SQLException if error occurs
     */
    public static void delete (Appointment appointment) throws SQLException {
        String query = Database.addTransactionCommit(Database.delete(table, "Appointment_ID"));

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, appointment.getId());

        preparedStatement.executeUpdate();
    }

    /**
     *
     * @return ArrayList of strings containing DISTINCT types.
     * @throws SQLException if sql has error
     */
    public static ArrayList<String> getTypes () throws SQLException {
        ArrayList<String> types = new ArrayList<>();
        String query = "SELECT DISTINCT appointments.Type FROM appointments;";

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            types.add(resultSet.getString("Type"));
        }

        return types;
    }

    /**
     *
     * @param type type of appointment to find.
     * @return ArrayList of MonthCountType
     * @throws SQLException if sql has error.
     */
    public static ArrayList<MonthCountType> getMonthByType (String type) throws SQLException {
        ArrayList<MonthCountType> monthCountTypes = new ArrayList<>();

        String query = "SELECT monthname(Start) as Month, COUNT(MONTH(Start)) as Count FROM appointments WHERE Type = ? GROUP BY monthname(Start);";

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setString(1, type);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final String month = resultSet.getString("Month");
            final int count = resultSet.getInt("Count");

            MonthCountType monthCountType = new MonthCountType(type, month, count);

            monthCountTypes.add(monthCountType);
        }

        return monthCountTypes;
    }
}
