package scheduleapp.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scheduleapp.model.division.FirstLevel;

/**
 * Static class for querying firstlevel divisions from db.
 */
public class FirstLevels {
    private static final String table = "first_level_divisions";

    /**
     *
     * @return tableName "first_level_divisions"
     */
    public static String getTable () {
        return table;
    }

    /**
     *
     * @param countryId reference id used to search table.
     * @return ArrayList of FirstLevel with countryId
     * @throws SQLException throws SQLException if error occurs
     */
    public static ArrayList<FirstLevel> getAllByCountryId (int countryId) throws SQLException {
        ArrayList<FirstLevel> firstLevelArrayList = new ArrayList<>();

        List<String> columns = Arrays.asList("Division_ID", "Division");
        String[] joins = new String[0];

        String[] referenceColumns = {"Country_ID"};

        String query = Database.select(getTable(), columns, joins, referenceColumns);

        PreparedStatement preparedStatement = Database.getPreparedStatement(query);

        preparedStatement.setInt(1, countryId);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            int id = resultSet.getInt("Division_ID");
            String division = resultSet.getString("Division");

            FirstLevel firstLevel = new FirstLevel(id, division, countryId);

            firstLevelArrayList.add(firstLevel);
        }

        return firstLevelArrayList;
    }
}
