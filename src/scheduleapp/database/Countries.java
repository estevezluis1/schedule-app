package scheduleapp.database;

import scheduleapp.model.division.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Static class for querying countries from db.
 */
public class Countries {
    private static final String table = "countries";

    /**
     *
     * @return table name "countries"
     */
    public static String getTable () {
        return table;
    }

    /**
     *
     * @param resultSet resultSet to parse columns (Country_ID, Country) from.
     * @return ArrayList of Country.
     * @throws SQLException throws SQLException if error occurs
     */
    private static ArrayList<Country> parseResultSet (ResultSet resultSet) throws SQLException {
        final ArrayList<Country> countryArrayList = new ArrayList<>();

        while (resultSet.next()) {
            final int id = resultSet.getInt("Country_ID");
            final String name = resultSet.getString("Country");

            Country country = new Country(id, name);

            countryArrayList.add(country);
        }

        return countryArrayList;
    }

    /**
     * Get all countries from database.
     * @return ArrayList of Countries
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<Country> getAll () throws SQLException {
        String query = "SELECT * FROM countries;";

        ResultSet resultSet = Database.getPreparedStatement(query).executeQuery();

        return parseResultSet(resultSet);
    }
}
