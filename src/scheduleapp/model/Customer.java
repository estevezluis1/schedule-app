package scheduleapp.model;

/**
 * Data  model for customer
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private String division;
    private int countryId;
    private String country;

    /**
     *
     * @param id id of customer.
     * @param name name of customer.
     * @param address address of customer.
     * @param postalCode postal code of customer.
     * @param phone phone number of customer.
     * @param divisionId division id of customer.
     * @param division division of customer.
     * @param countryId country id of customer.
     * @param country country name of customer.
     */
    public Customer(int id, String name, String address, String postalCode, String phone, int divisionId, String division, int countryId, String country) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setPostalCode(postalCode);
        this.setPhone(phone);
        this.setDivisionId(divisionId);
        this.setDivision(division);
        this.setCountryId(countryId);
        this.setCountry(country);
    }

    /**
     *
     * @param name name of customer to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param id of customer.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @param address address of customer to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @param postalCode postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     *
     * @param phone phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @param divisionId divisionId to set.
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     *
     * @return name.
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return id.
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @return address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     *
     * @return get postalCode
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    /**
     *
     * @return phone number.
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     *
     * @return division id.
     */
    public int getDivisionId() {
        return this.divisionId;
    }

    /**
     *
     * @param division division to set.
     */
    public void setDivision (String division) {
        this.division = division;
    }

    /**
     *
     * @return division
     */
    public String getDivision () {
        return this.division;
    }

    /**
     *
     * @param countryId set countryId
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     *
     * @return countryId customer located.
     */
    public int getCountryId () {
        return this.countryId;
    }

    /**
     *
     * @param country country name to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return country name
     */
    public String getCountry () {
        return this.country;
    }
}
