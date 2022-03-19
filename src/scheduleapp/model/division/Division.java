package scheduleapp.model.division;

/**
 * Data model for Division, including Country, FirstLevel
 */
public class Division {
    private int id;
    private String name;

    /**
     *
     * @param id id of division to set
     * @param name name of division to set
     */
    public Division(int id, String name) {
        this.setId(id);
        this.setName(name);
    }

    /**
     *
     * @return id of division
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @param id id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return name of division
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param name name of division
     */
    public void setName(String name) {
        this.name = name;
    }
}
