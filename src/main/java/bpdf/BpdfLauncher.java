package bpdf;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class BpdfLauncher {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
        Option load_graph = Option.builder("l").hasArg().argName("BPDF graph file").longOpt("load")
                                               .desc("load graph from file").build();
        Option config_graph = Option.builder("c").hasArg().argName("config file").longOpt("cfg")
                                                 .desc("configure graph with file").build();
        // Add options
        options.addOption(help);
        options.addOption(gui);
        options.addOption(load_graph);
        options.addOption(config_graph);

        // Parse options
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("BPDF", options);
                System.exit(0);
            }

            if (cmd.hasOption("g")) {
                status.gui = true;
            }

            if (cmd.hasOption("l")) {
                status.path = cmd.getOptionValue("l");
            }

            if (cmd.hasOption("c")) {
                status.config = cmd.getOptionValue("c");
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
