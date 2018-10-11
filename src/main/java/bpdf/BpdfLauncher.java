package bpdf;

import bpdf.graph.BPDFGui;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

public class BpdfLauncher {
    public static void main(String[] args) {
        // Create options
        Options options = new Options();
        Option help = new Option("h", "help", false, "Help");
        Option gui = new Option("g", "gui", false, "Enable GUI");
        Option loadgraph = Option.builder("l").hasArg()
                                              .argName( "filename" )
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
            } else if(cmd.hasOption("g")) {
                // Launch the GUI
                BPDFGui bpdfgui = new BPDFGui();
            } else if(cmd.hasOption("l")) {
                String path = cmd.getOptionValues("l")[0];
                System.out.println("Running graph at " + path);
            } else {
            }
        } catch (ParseException exc) {
            System.err.println("Invalid arguments: " + exc);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BPDF", options);
            System.exit(1);
        }
    }
}
