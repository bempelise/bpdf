package bpdf;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class BpdfLogger {
    private static FileHandler fh;
    private static ConsoleHandler ch;

    public static void setup() throws IOException {
        // Remove default handler
        LogManager.getLogManager().reset();
        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        Formatter formatter = new Formatter() {

            @Override
            public String format(LogRecord arg0) {
                StringBuilder strb = new StringBuilder();
                String msg = String.format("[%-7s] %-20s: %s",
                                           arg0.getLevel(),
                                           arg0.getSourceClassName(),
                                           arg0.getMessage());
                strb.append(msg);
                strb.append(System.getProperty("line.separator"));
                return strb.toString();
            }
        };

        logger.setLevel(Level.INFO);

        // Create a Console formatter
        ch = new ConsoleHandler();
        ch.setFormatter(formatter);
        logger.addHandler(ch);

        // create a TXT formatter
        fh = new FileHandler("bpdf.log");
        fh.setFormatter(formatter);
        logger.addHandler(fh);
    }
}
