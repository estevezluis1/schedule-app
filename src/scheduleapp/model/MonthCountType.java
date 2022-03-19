package scheduleapp.model;

/**
 * Data model for MonthCountType
 */
public class MonthCountType {
    private String type;
    private String month;
    private int monthCount;

    /**
     *
     * @param type appointment type
     * @param month appointment month
     * @param monthCount int count of type in month
     */
    public MonthCountType(String type, String month, int monthCount) {
        this.setType(type);
        this.setMonth(month);
        this.setMonthCount(monthCount);
    }

    /**
     *
     * @return type;
     */
    public String getType () {
        return this.type;
    }

    /**
     *
     * @return return month
     */
    public String getMonth() {
        return month;
    }

    /**
     *
     * @return monthCount
     */
    public int getMonthCount() {
        return monthCount;
    }

    /**
     *
     * @param type set type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @param month set month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     *
     * @param monthCount set monthCount
     */
    public void setMonthCount(int monthCount) {
        this.monthCount = monthCount;
    }
}
