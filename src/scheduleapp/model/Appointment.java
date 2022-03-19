package scheduleapp.model;

import java.time.LocalDateTime;

/**
 * Data model for appointment.
 */
public class Appointment {
    private int id;
    private int userId;
    private int contactId;
    private int customerId;

    private String type;
    private String title;
    private String location;
    private String description;
    private String createdBy;

    // localDateTime MUST be in System default timezone.
    private LocalDateTime startLocalDateTime;
    private LocalDateTime endLocalDateTime;

    /**
     *
     * @param id id of appointment.
     * @param title title of appointment.
     * @param description description of appointment.
     * @param location location of appointment.
     * @param type type of appointment.
     * @param customerId customerId of appointment.
     * @param userId userId of appointment.
     * @param contactId contactId of appointment.
     * @param createdBy createdBy of appointment.
     * @param start start of appointment.
     * @param end end of appointment.
     */
    public Appointment(int id, String title, String description, String location, String type,
                       int customerId, int userId, int contactId, String createdBy, LocalDateTime start, LocalDateTime end) {
        this.setId(id);
        this.setUserId(userId);
        this.setContactId(contactId);
        this.setCustomerId(customerId);
        this.setType(type);
        this.setTitle(title);
        this.setLocation(location);
        this.setDescription(description);
        this.setCreatedBy(createdBy);
        this.setStartLocalDateTime(start);
        this.setEndLocalDateTime(end);
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
     * @param userId userId to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     *
     * @param contactId contactId to set.
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     *
     * @param customerId customerId to set.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     *
     * @param title title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param description description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @param location location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @param type type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @param createdBy createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *
     * @param endLocalDateTime endLocalDateTime to set.
     */
    public void setEndLocalDateTime(LocalDateTime endLocalDateTime) {
        this.endLocalDateTime = endLocalDateTime;
    }

    /**
     *
     * @param startLocalDateTime startLocalDateTime to set.
     */
    public void setStartLocalDateTime(LocalDateTime startLocalDateTime) {
        this.startLocalDateTime = startLocalDateTime;
    }

    /**
     *
     * @return id of appointment.
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return customer id.
     */
    public int getCustomerId () {
        return this.customerId;
    }

    /**
     *
     * @return user id.
     */
    public int getUserId () {
        return this.userId;
    }

    /**
     *
     * @return contact id.
     */
    public int getContactId () {
        return this.contactId;
    }

    /**
     *
     * @return type.
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return title.
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return location.
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return createdBy.
     */
    public String getCreatedBy () {
        return this.createdBy;
    }

    /**
     *
     * @return endLocalDateTime
     */
    public LocalDateTime getEndLocalDateTime() {
        return endLocalDateTime;
    }

    /**
     *
     * @return startLocalDateTime
     */
    public LocalDateTime getStartLocalDateTime() {
        return startLocalDateTime;
    }
}
