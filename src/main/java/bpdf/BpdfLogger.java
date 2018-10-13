package bpdf;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class BpdfLogger {
    static private FileHandler fh;
    static private ConsoleHandler ch;

    static public void setup() throws IOException {
        // Remove default handler
        LogManager.getLogManager().reset();
        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        Formatter formatter = new Formatter() {

            @Override
            public String format(LogRecord arg0) {
                StringBuilder b = new StringBuilder();
                b.append("[");
                b.append(arg0.getLevel());
                b.append("] ");
                b.append(arg0.getSourceMethodName());
                b.append(": ");
                b.append(arg0.getMessage());
                b.append(System.getProperty("line.separator"));
                return b.toString();
            }

        };

        logger.setLevel(Level.INFO);

        // Create a Console formatter
        ch = new ConsoleHandler();
        ch.setFormatter(formatter);
        logger.addHandler(ch);

        // create a TXT formatter
        fh = new FileHandler("BpdfLog.txt");
        fh.setFormatter(formatter);
        logger.addHandler(fh);
    }
}
