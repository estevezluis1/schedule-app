package scheduleapp.model;

/**
 * Data model for User
 */
public class User {
    private int id;
    private String username;

    /**
     *
     * @param id id to set
     * @param username username to set
     */
    public User(int id, String username) {
        this.setId(id);
        this.setUsername(username);
    }

    /**
     *
     * @param id id of user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @param username username of user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return id of user
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @return username
     */
    public String getUsername() {
        return this.username;
    }
}
