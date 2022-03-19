package scheduleapp.database;
import java.sql.*;
import java.util.List;

public class Database {
    public enum COMMAND {
        INSERT,
        UPDATE
    }

    private static final String databaseName = "client_schedule";
    private static final String databaseUser = "sqlUser";
    private static final String databasePassword = "Passw0rd!";

    private static final String databaseUrl = "jdbc:mysql://localhost/" + databaseName + "?connectionTimeZone=UTC&allowMultiQueries=true";

    private static Connection connection;

    public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";

    /**
     * method for opening database connection.
     */
    public static void openConnection () {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);

            connection.setAutoCommit(false);


        } catch (ClassNotFoundException | SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * method for closing database connection.
     * @throws SQLException throws sql exception is problems closing database connection.
     */
    public static void closeConnection () throws SQLException {
        if (connection.isClosed()) {
            return;
        }

        connection.close();
    }

    /**
     *
     * @param query query get preparedStatement.
     * @return PreparedStatement
     * @throws SQLException throws sqlexception if error getting preparedStatement.
     */
    public static PreparedStatement getPreparedStatement (String query) throws SQLException {
        System.out.println(query);
        return connection.prepareStatement(query);
    }

    /**
     *
     * Used for when inserting data and retrieve last insert id.
     *
     * @param query query get preparedStatement.
     * @return PreparedStatement with return generated keys
     * @throws SQLException throws sqlException if error getting preparedStatement.
     */
    public static PreparedStatement getInsertPreparedStatement (String query) throws SQLException {
        System.out.println(query);
        return connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * surrounds query with transaction and commit.
     * Used for query which updates database (insert, update).
     * @param query query to surround.
     * @return transaction commit query.
     */
    public static String addTransactionCommit (String query) {
        return "START TRANSACTION;" + query + "COMMIT;";
    }

    /**
     *
     * @param table table to search.
     * @param columns columns to select.
     * @param joins if have joins, append to query.
     * @param referenceColumns reference column used to search table.
     * @return sql SELECT query.
     */
    public static String select (String table, List<String> columns, String[] joins, String[] referenceColumns) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT ");

        stringBuilder.append(columns.size() > 0 ? String.join(",", columns) : "*");

        stringBuilder.append(" FROM ").append(table);

        if (joins.length > 0) {
            stringBuilder.append(" ").append(String.join(" ", joins));
        }

        if (referenceColumns.length > 0) {
            stringBuilder.append(" WHERE ").append(String.join("=? AND ", referenceColumns)).append("=?");
        }

        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    /**
     *
     * @param table table name to build sql query.
     * @param columns columns to update
     * @param referenceColumn referenceColumn used to search for data to update in sql query.
     * @return sql UPDATE query.
     */
    public static String update (String table, List<String> columns, String referenceColumn) {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("UPDATE ").append(table).append(" SET ");

        for (int i = 0; i < columns.size(); i++) {
            queryBuilder.append(columns.get(i)).append("=?").append(i != columns.size() - 1 ? "," : "");
        }

        queryBuilder.append(" WHERE ").append(referenceColumn).append("=?;");

        return queryBuilder.toString();
    }

    /**
     *
     * @param table table name to build sql query.
     * @param referenceColumn referenceColumn used to search in sql query.
     * @return SQL query
     */
    public static String delete (String table, String referenceColumn) {
        return "DELETE FROM " + table + " WHERE " + referenceColumn + "=?;";
    }

    /**
     *
     * @param table table name to build query.
     * @param columns to build query.
     * @return SQL query.
     */
    public static String insert (String table, List<String> columns) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ").append(table);
        query.append(" (").append(String.join(",", columns)).append(") ");

        query.append(" VALUES (");

        for (int i = 0; i < columns.size(); i++) {
            query.append("?").append(i != columns.size() -1 ? ",": "");
        }

        query.append(");");

        return query.toString();
    }
}
