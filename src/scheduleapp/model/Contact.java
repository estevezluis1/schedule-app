package scheduleapp.model;

/**
 * Data model for contact
 */
public class Contact {
    private int id;
    private String name;
    private String email;

    /**
     *
     * @param id id of contact
     * @param name name of contact
     * @param email email address of contact
     */
    public Contact(int id, String name, String email) {
        this.setId(id);
        this.setName(name);
        this.setEmail(email);
    }

    /**
     *
     * @param id set id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @param name set name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param email set email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return id of contact
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @return name of contact
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return email address of contact
     */
    public String getEmail() {
        return this.email;
    }
}
