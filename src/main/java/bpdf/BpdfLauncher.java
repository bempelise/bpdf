package bpdf;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class BpdfLauncher {
    private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        try {
            BpdfLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger initilization error");
        }

        BpdfStatus status = new BpdfStatus();
        Options options = new Options();
        Option help = new Option("h", "help", false, "help");
        Option gui = new Option("g", "gui", false, "Enable GUI");
        Option loadgraph = Option.builder("l").hasArg()
                                              .argName("filename")
                                              .longOpt("load")
                                              .desc("load graph from file")
                                              .build();
        // Add options
        options.addOption(help);
        options.addOption(gui);
        options.addOption(loadgraph);

        // Parse options
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("BPDF", options);
                System.exit(0);
            }

            if(cmd.hasOption("g")) {
                status.gui = true;
            }

            if(cmd.hasOption("l")) {
                status.path = cmd.getOptionValue("l");
            }
        } catch (ParseException exc) {
            LOG.warning("Invalid arguments: " + exc);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BPDF", options);
            System.exit(1);
        }
        LOG.info("Launching Manager");
        BpdfManager manager = new BpdfManager(status);
    }
}
