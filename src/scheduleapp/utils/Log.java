package scheduleapp.utils;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Log class is used as login activity logger.
 */
public class Log {
    private final static Logger logger = Logger.getLogger("Schedule Application");

    /**
     * set fileHandler for logger to log to file.
     */
    public static void init () {
        try {
            FileHandler fileHandler = new FileHandler("login_activity.txt", true);

            logger.addHandler(fileHandler);

            SimpleFormatter simpleFormatter = new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    ZonedDateTime zonedDateTime = record.getInstant().atZone(ZoneId.of("UTC"));

                    return String.format("[%1$tF %1$tT UTC] [LOGIN] %3$s %n", zonedDateTime, record.getLevel().getLocalizedName(), record.getMessage());
                }
            };

            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException | SecurityException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     *
     * @param msg msg to write.
     */
    public static void info (String msg) {
        logger.info(msg);
    }
}
