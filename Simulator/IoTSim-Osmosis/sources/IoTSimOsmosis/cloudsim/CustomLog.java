package IoTSimOsmosis.cloudsim;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import IoTSimOsmosis.cloudsim.core.CloudSim;
import IoTSimOsmosis.cloudsim.util.InMemoryBufferredHandler;
import IoTSimOsmosis.cloudsim.util.TextUtil;
import org.apache.commons.io.output.NullOutputStream;

import com.google.common.base.Function;

/**
 * Replaces the primitive functionality of the standard CloudSim Log. Allows
 * easily to redirect log to a file, and to customize the output.
 * 
 * <br/>
 * 
 * Before using this class remember to call the {@link CustomLog.configLogger}
 * with the desired {@link Properties}, or with an empty {@link Properties} if
 * you want to use the defaults. The documentation of the public String
 * constants of the class describes what keys and values can be specified.
 * 
 * 
 * @author Nikolay Grozev
 * 
 *         Adapted from versions of:
 *         <ol>
 *         <li>Anton Beloglazov</li>
 *         <li>William Voorsluys</li>
 *         <li>Adel Nadjaran Toosi</li>
 *         </ol>
 * 
 * @since CloudSim Toolkit 2.0
 */
public class CustomLog {

    // //// Configuration properties
    /**
     * A key for the config property specifying what is the minimal logged
     * level. Must be a string of a constant from {@link Level}
     */
    public static final String LOG_LEVEL_PROP_KEY = "LogLevel";

    /**
     * A key for a boolean property, specifying if every log entry should
     * contain the current CloudSim time.
     */
    public static final String LOG_CLOUD_SIM_CLOCK_PROP_KEY = "LogCloudSimClock";

    /**
     * A key for a boolean property, specifying if every log entry should
     * contain with the current CloudSim time in a readable format.
     */
    public static final String LOG_READABLE_CLOUD_SIM_CLOCK_PROP_KEY = "LogReadableSimClock";

    /**
     * A key for a boolean property, specifying if every log entry should
     * contain the current CloudSim time.
     */
    public static final String LOG_CLOUD_REAL_TIME_PROP_KEY = "LogRealTimeClock";

    /**
     * Specifies which methods of the {@link LogRecord} class should be used to
     * create the log entries. Calls must be seprated with a semicolon ";". For
     * example "getLevel;getMessage".
     */
    public static final String LOG_FORMAT_PROP_KEY = "LogFormat";

    /**
     * A key for a file property where the log is to be written. If not
     * specified the standard output is used instead of a file.
     */
    public static final String FILE_PATH_PROP_KEY = "FilePath";

    /**
     * A key for a boolean property specifying whether the standard CloudSim
     * logger should be turned off. That will cause all the log generated by the
     * system classes of CloudSim not to be printed.
     */
    public static final String SHUT_STANDART_LOGGER_PROP_KEY = "ShutStandardLogger";

    /**
     * A key for an integer property, specifying how the size of the log buffer
     * - i.e. how many records to be buffered before an actual write is
     * performed. If not specified - no buffering is used.
     */
    private static final String BUFFER_SIZE_PROP_KEY = "BufferSize";

    /**
     * The default log level used by this log, if not specified.
     */
    public final static Level DEFAULT_LEVEL = Level.INFO;

    private static final Logger LOGGER = Logger.getLogger(CustomLog.class.getPackage().getName());

    /** Buffer to avoid creating new string builder upon every print. */
    private static StringBuilder builder = new StringBuilder();

    private static Level granularityLevel = DEFAULT_LEVEL;
    private static Formatter formatter;
    private static int bufferSize = -1;

    /**
     * Prints the message passed as an object. Simply uses toString
     * implementation.
     * 
     * @param level
     *            - the level to use. If null the default level is used.
     * @param message
     *            - the message.
     */
    public static void print(final Level level, final Object message) {
        if (isLevelHighEnough(level)) {
            LOGGER.log(level == null ? DEFAULT_LEVEL : level, String.valueOf(message));
        }
    }

    /**
     * Prints the message passed as an object. Simply uses toString
     * implementation. Uses the default log level.
     * 
     * @param message
     *            - the message.
     */
    public static void print(final Object message) {
        print(DEFAULT_LEVEL, message);
    }

    /**
     * Prints the concatenated messages.
     * 
     * @param level
     *            - the level to use. If null the default level is used.
     * @param messages
     *            - the messages.
     */
    public static void printConcat(final Level level, final Object... messages) {
        if (isLevelHighEnough(level)) {
            builder.setLength(0); // Clear the buffer
            for (int i = 0; i < messages.length; i++) {
                builder.append(String.valueOf(messages[i]));
            }
            LOGGER.log(level == null ? DEFAULT_LEVEL : level, String.valueOf(builder));
        }
    }

    /**
     * Prints the concatenated messages.
     * 
     * @param messages
     *            - the messages.
     */
    public static void printConcat(final Object... messages) {
        printConcat(DEFAULT_LEVEL, messages);
    }

    /**
     * Prints a line with the concatenated messages.
     * 
     * @param level
     *            - the log level. If null, the default log level is used.
     * @param messages
     *            - the messages.
     */
    public static void printConcatLine(final Level level, final Object... messages) {
        if (isLevelHighEnough(level)) {
            builder.setLength(0); // Clear the buffer
            for (int i = 0; i < messages.length; i++) {
                builder.append(String.valueOf(messages[i]));
            }
            LOGGER.log(level == null ? DEFAULT_LEVEL : level, String.valueOf(builder));
        }
    }

    /**
     * Prints a line with the concatenated messages.
     * 
     * @param messages
     *            - the messages.
     */
    public static void printConcatLine(final Object... messages) {
        printConcatLine(DEFAULT_LEVEL, messages);
    }

    /**
     * Prints a line with the message to the log.
     * 
     * @param level
     *            - the log level. If null, the default log level is used.
     * @param msg
     *            - the message. Must not be null.
     */
    public static void printLine(final Level level, final String msg) {
        if (isLevelHighEnough(level)) {
            LOGGER.log(level == null ? DEFAULT_LEVEL : level, msg);
        }
    }

    /**
     * Prints a line with the message to the log. Uses the default log level.
     * 
     * @param msg
     *            - the message. Must not be null.
     */
    public static void printLine(final String msg) {
        printLine(DEFAULT_LEVEL, msg);
    }

    /**
     * Prints the formatted string, resulting from applying the format string to
     * the arguements.
     * 
     * @param format
     *            - the format (as in String.format).
     * @param level
     *            - the level. If null the default level is used
     * @param args
     */
    public static void printf(final Level level, final String format, final Object... args) {
        if (isLevelHighEnough(level)) {
            LOGGER.log(level == null ? DEFAULT_LEVEL : level, String.format(format, args));
        }
    }

    public static boolean isLevelHighEnough(final Level level) {
        return (level == null && DEFAULT_LEVEL.intValue() >= granularityLevel.intValue())
                || (level != null && level.intValue() >= granularityLevel.intValue());
    }

    /**
     * Prints the formatted string with the default level, resulting from
     * applying the format string to the arguments.
     * 
     * @param format
     *            - the format (as in String.format).
     * @param args
     */
    public static void printf(final String format, final Object... args) {
        printf(DEFAULT_LEVEL, String.format(format, args));
    }

    /**
     * Prints a header for the specified class. The format is as per the
     * specification in {@link TextUtil}
     * 
     * @param klass
     *            - the class. Must not be null.
     * @param delim
     *            - the delimeter. Must not be null.
     * @param properties
     *            - the properties to use for the header.
     */
    public static void printHeader(final Class<?> klass, final String delim, final String[] properties) {
        CustomLog.printLine(TextUtil.getCaptionLine(klass, delim, properties));
    }

    /**
     * Prints a header for the specified class. The format is as per the
     * specification in {@link TextUtil}
     * 
     * @param klass
     *            - the class. Must not be null.
     * @param delim
     *            - the delimeter. Must not be null.
     * @param properties
     *            - the properties to use for the header.
     */
    public static void printHeader(final Class<?> klass, final String delim) {
        CustomLog.printLine(TextUtil.getCaptionLine(klass, delim));
    }

    /**
     * Prints a header for the specified class. The format is as per the
     * specification in {@link TextUtil}
     * 
     * @param klass
     *            - the class. Must not be null.
     */
    public static void printHeader(final Class<?> klass) {
        CustomLog.printLine(TextUtil.getCaptionLine(klass));
    }

    /**
     * Prints a line for the object. The format is as per the specification in
     * {@link TextUtil}
     * 
     * @param o
     *            - the object. Must not be null.
     * @param delim
     *            - the delimeter. Must not be null.
     * @param properties
     *            - the properties to print. If null the default props are used.
     */
    public static void printLineForObject(final Object o, final String delim, String[] properties) {
        CustomLog.print(TextUtil.getTxtLine(o, delim, properties));
    }

    /**
     * Prints a line for the object. The format is as per the specification in
     * {@link TextUtil}
     * 
     * @param o
     *            - the object. Must not be null.
     */
    public static void printLineForObject(final Object o) {
        CustomLog.print(TextUtil.getTxtLine(o));
    }

    /**
     * Prints a line for the object. The format is as per the specification in
     * {@link TextUtil}
     * 
     * @param o
     *            - the object. Must not be null.
     * @param delim
     *            - the delimeter to use.
     */
    public static void printLineForObject(final Object o, final String delim) {
        CustomLog.print(TextUtil.getTxtLine(o, delim));
    }

    /**
     * Prints the objects' details with a header in a CSV - like format.
     * 
     * @param klass
     *            - the class to be used for the header. If null no header is
     *            printed.
     * @param properties
     *            - the properties to print.
     * @param list
     *            - list of objects. All objects, must be of type klass.
     */
    public static void printResults(final Class<?> klass, String[] properties, final List<?>... lines) {
        if (klass != null) {
            // Print header line
            printHeader(klass, TextUtil.DEFAULT_DELIM, properties);
        }

        // Print details for each element
        for (List<?> list : lines) {
            for (Object o : list) {
                printLineForObject(o, TextUtil.DEFAULT_DELIM, properties);
            }
        }
    }

    @SafeVarargs
    public static <F> void printResults(final Class<? extends F> klass, String[] properties,
            final LinkedHashMap<String, Function<? extends F, String>> virtualProps, final List<F>... lines) {
        if (klass != null) {
            // Print header line
            CustomLog.printLine(TextUtil.getCaptionLine(klass, TextUtil.DEFAULT_DELIM, properties, virtualProps
                    .keySet().toArray(new String[virtualProps.size()])));
        }

        // Print details for each element
        for (List<F> list : lines) {
            for (F o : list) {
                CustomLog.print(TextUtil.getTxtLine(o, TextUtil.DEFAULT_DELIM, properties, false, virtualProps));
            }
        }
    }

    /**
     * Prints the objects' details with a header in a CSV - like format.
     * 
     * @param klass
     *            - the class to be used for the header. If null no header is
     *            printed.
     * @param list
     *            - list of objects. All objects, must be of type klass.
     */
    public static void printResults(final Class<?> klass, final List<?>... lines) {
        if (klass != null) {
            // Print header line
            printHeader(klass);
        }

        // Print details for each cloudlet
        for (List<?> list : lines) {
            for (Object o : list) {
                printLineForObject(o);
            }
        }
    }

    @SafeVarargs
    public static <F> void printResults(final Class<? extends F> klass,
            final LinkedHashMap<String, Function<? extends F, String>> virtualProps, final List<F>... lines) {
        if (klass != null) {
            // Print header line
            CustomLog.printLine(TextUtil.getCaptionLine(klass, TextUtil.DEFAULT_DELIM, null, virtualProps.keySet()
                    .toArray(new String[virtualProps.size()])));
        }

        // Print details for each cloudlet
        for (List<F> list : lines) {
            for (F o : list) {
                CustomLog.print(TextUtil.getTxtLine(o, TextUtil.DEFAULT_DELIM, null, false, virtualProps));
            }
        }
    }

    /**
     * Prints the objects' details with a header in a CSV - like format.
     * 
     * @param klass
     *            - the class to be used for the header. If null no header is
     *            printed.
     * @param delim
     *            - the delimeter to use.
     * @param list
     *            - list of objects. All objects, must be of type klass.
     */
    public static void printResults(final Class<?> klass, final String delim, final List<?>... lines) {
    	if (klass != null) {
            // Print header line
            CustomLog.printLine(TextUtil.getCaptionLine(klass, delim, null));
        }

        // Print details for each cloudlet
        for (List<?> list : lines) {
            for (Object o : list) {
                CustomLog.print(TextUtil.getTxtLine(o, delim, null, false));
            }
        }
    }

    /**
     * Prints the objects' details with a header in a CSV - like format.
     * 
     * @param klass
     *            - the class to be used for the header. If null no header is
     *            printed.
     * @param delim
     *            - the delimeter to use.
     * @param properties
     *            - the properties to print.
     * @param list
     *            - list of objects. All objects, must be of type klass.
     */
    public static void printResults(final Class<?> klass, final String delim, final String[] properties,
            final List<?>... lines) {
        if (klass != null) {
            // Print header line
            printHeader(klass, delim, properties);
        }

        // Print details for each cloudlet
        printResultsWithoutHeader(klass, delim, properties, lines);
    }

    /**
     * Prints the objects' details without a header in a CSV - like format.
     * 
     * @param klass
     *            - the class to be used for the header. If null no header is
     *            printed.
     * @param delim
     *            - the delimeter to use.
     * @param properties
     *            - the properties to print.
     * @param list
     *            - list of objects. All objects, must be of type klass.
     */
    public static void printResultsWithoutHeader(final Class<?> klass, final String delim, final String[] properties,
            final List<?>... lines) {
        // Print details for each cloudlet
        for (List<?> list : lines) {
            for (Object o : list) {
                printLineForObject(o, delim, properties);
            }
        }
    }

    /**
     * Prints the objects' details without a header in a CSV - like format.
     * 
     * @param klass
     *            - the class to be used for the header. If null no header is
     *            printed.
     * @param delim
     *            - the delimeter to use.
     * @param list
     *            - list of objects. All objects, must be of type klass.
     */
    public static void printResultsWithoutHeader(final Class<?> klass, final String delim, final List<?>... lines) {
        // Print details for each cloudlet
        for (List<?> list : lines) {
            for (Object o : list) {
                printLineForObject(o, delim);
            }
        }
    }

    /**
     * Logs the stacktrace of the exception.
     * 
     * @param level
     *            - the level to use.
     * @param message
     *            - the messag to append before that.
     * @param exc
     *            - the exception to log.
     */
    public static void logError(final Level level, final String message, final Throwable exc) {
        if (isLevelHighEnough(level)) {
            LOGGER.log(level, message, exc);
        }
    }

    /**
     * Logs the stacktrace of the exception.
     * 
     * @param message
     *            - the messag to append before that.
     * @param exc
     *            - the exception to log.
     */
    public static void logError(final String message, final Throwable exc) {
        logError(DEFAULT_LEVEL, message, exc);
    }

    /**
     * Returns if this logger is disabled.
     * 
     * @return - if this logger is disabled.
     */
    public static boolean isDisabled() {
        return LOGGER.getLevel().equals(Level.OFF);
    }

    /**
     * Sets the output of this logger. This method is to be used for redirecting
     * to "nonstandard" (e.g. database) output streams. If you simply want to
     * redirect the logger to a file, you'd better use the initialization
     * properties.
     * 
     * @param output
     *            - the new output. Must not be null.
     */
    public static void setOutput(final OutputStream output) {
        LOGGER.addHandler(new StreamHandler(output, formatter));
    }

    /**
     * Configures the logger. Must be called before the logger is used.
     * 
     * @param props
     *            - the configuration properties. See the predefined keys in
     *            this class, to get an idea of what is required.
     * @throws SecurityException
     *             - if the specified log format contains invalid method calls.
     * @throws IOException
     *             - if something goes wrong with the I/O.
     */
    public static void configLogger(final Properties props) throws SecurityException, IOException {
        final String fileName = props.containsKey(FILE_PATH_PROP_KEY) ? props.getProperty(FILE_PATH_PROP_KEY)
                .toString() : null;
        final String format = props.getProperty(LOG_FORMAT_PROP_KEY, "getLevel;getMessage").toString().trim();
        final boolean prefixCloudSimClock = Boolean.parseBoolean(props
                .getProperty(LOG_CLOUD_SIM_CLOCK_PROP_KEY, "false").toString().trim());
        final boolean prefixReadableCloudSimClock = Boolean.parseBoolean(props
                .getProperty(LOG_READABLE_CLOUD_SIM_CLOCK_PROP_KEY, "false").toString().trim());
        final boolean prefixRealTimeClock = Boolean.parseBoolean(props
                .getProperty(LOG_CLOUD_REAL_TIME_PROP_KEY, "false").toString().trim());
        final boolean shutStandardMessages = Boolean.parseBoolean(props
                .getProperty(SHUT_STANDART_LOGGER_PROP_KEY, "false").toString().trim());
        granularityLevel = Level.parse(props.getProperty(LOG_LEVEL_PROP_KEY, DEFAULT_LEVEL.getName()).toString());
        bufferSize = Integer.parseInt(props.getProperty(BUFFER_SIZE_PROP_KEY, "-1").toString().trim());

        if (shutStandardMessages) {
            Log.setOutput(new NullOutputStream());
            Log.disable();
        }

        LOGGER.setUseParentHandlers(false);
        formatter = new CustomFormatter(prefixCloudSimClock, prefixReadableCloudSimClock, prefixRealTimeClock, format);

        redirectToFile(fileName);
    }

    /**
     * Redirects this logger to a file.
     * 
     * @param fileName
     *            - the name of the new log file. If null the log is redirected
     *            to the standard output.
     */
    public static void redirectToFile(final String fileName) {
        redirectToFile(fileName, false);
    }

    /**
     * Redirects this logger to a file.
     * 
     * @param fileName
     *            - the name of the new log file. If null the log is redirected
     *            to the standard output.
     * @param append
     *            specifies append mode
     * 
     */
    public static void redirectToFile(final String fileName, Boolean append) {
        closeAndRemoveHandlers();

        if (fileName != null) {
            System.err.println("Rediricting output to " + new File(fileName).getAbsolutePath());
        }

        try {
            Handler handler = fileName != null ? new FileHandler(fileName, append) : new ConsoleHandler();
            handler.setLevel(granularityLevel);
            handler.setFormatter(formatter);

            Handler bufferedHandler = buffer(handler);

            LOGGER.addHandler(bufferedHandler);
            LOGGER.setLevel(granularityLevel);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Redirects this logger to the standard output.
     */
    public static void redirectToConsole() {
        redirectToFile(null);
    }

    private static Handler buffer(Handler handler) {
        Handler wrapHandler = bufferSize > 0 ? new InMemoryBufferredHandler(handler, bufferSize) : handler;
        return wrapHandler;
    }

    /**
     * Close and remove all file handlers. You should call this before exiting
     * the program
     */
    public static void closeAndRemoveHandlers() {
        for (Handler h : LOGGER.getHandlers()) {
            LOGGER.removeHandler(h);
            h.flush();
            h.close();
        }
    }

    /**
     * Flushes the buffers, if any.
     */
    public static void flush() {
        for (Handler h : LOGGER.getHandlers()) {
            h.flush();
        }
    }

    private static class CustomFormatter extends Formatter {

        private final boolean prefixCloudSimClock;
        private final boolean prefixReadableCloudSimClock;
        private final boolean prefixRealTimeClock;
        private final String format;
        SimpleFormatter defaultFormatter = new SimpleFormatter();

        public CustomFormatter(final boolean prefixCloudSimClock, final boolean prefixReadableCloudSimClock,
                final boolean prefixRealTimeClock, final String format) {
            super();
            this.prefixCloudSimClock = prefixCloudSimClock;
            this.prefixReadableCloudSimClock = prefixReadableCloudSimClock;
            this.prefixRealTimeClock = prefixRealTimeClock;
            this.format = format;
        }

        @Override
        public String format(final LogRecord record) {
            final String[] methodCalls = format.split(";");
            final StringBuffer result = new StringBuffer();
            if (prefixRealTimeClock) {
                result.append(TextUtil.getTimeFormat().format(new Date(record.getMillis())) + "\t");
            }
            if (prefixCloudSimClock) {
                result.append(TextUtil.toString(CloudSim.clock()) + "\t");
            }
            if (prefixReadableCloudSimClock) {
                result.append(TextUtil.getReadableTime(CloudSim.clock()) + "\t");
            }

            // If there is an exception - use the standard formatter
            if (record.getThrown() != null) {
                result.append(defaultFormatter.format(record));
            } else {
                int i = 0;
                for (String method : methodCalls) {
                    try {
                        result.append(record.getClass().getMethod(method).invoke(record));
                    } catch (Exception e) {
                        System.err.println("Error in logging:");
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                    if (i++ < methodCalls.length - 1) {
                        result.append('\t');
                    }
                }
            }

            result.append(TextUtil.NEW_LINE);

            return result.toString();
        }
    }
}