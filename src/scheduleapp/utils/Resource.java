package scheduleapp.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * static resource to be accessed throughout ScheduleApp.
 */
public class Resource {
    private static ResourceBundle resourceBundle;

    /**
     * Default resource bundle is english in case locale isn't French or English language.
     *
     * @param locale Locale to set.
     */
    public static void setResourceLocale (Locale locale) {
        resourceBundle = ResourceBundle.getBundle("login", locale);
    }

    /**
     *
     * @param key property/key to lookup resource.
     * @return String
     */
    public static String getValue (String key) {
        return resourceBundle.getString(key);
    }
}
