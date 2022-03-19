package scheduleapp.model.division;

/**
 * Data model for FirstLevel Division, extends from Division.
 */
public class FirstLevel extends Division {
    private int countryId;

    /**
     * Extends Division, however is a lower division of country.
     * @param id id of First level.
     * @param name name of first level.
     * @param countryId country id that belongs to first level.
     */
    public FirstLevel(int id, String name, int countryId) {
        super(id, name);

        this.setCountryId(countryId);
    }

    /**
     *
     * @param countryId country id to set
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     *
     * @return country id
     */
    public int getCountryId() {
        return this.countryId;
    }
}
