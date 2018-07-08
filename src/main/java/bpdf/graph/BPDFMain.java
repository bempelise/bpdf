// BPDFMain.java
package bpdf.graph;

import javax.swing.JFrame;
import org.apache.commons.cli.*;

public class BPDFMain {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "Help");
        options.addOption("ng", "no-gui", false, "Disable GUI");

        Option loadgraph = Option.builder("l").hasArg()
                                    .argName( "filename" )
                                    .longOpt("load")
                                    .desc( "load graph from file" )
                                    .build();
        options.addOption(loadgraph);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("BPDF", options);
            } else if(cmd.hasOption("ng")) {
                // Run without a GUI
            } else if(cmd.hasOption("l")) {
                String path = cmd.getOptionValues("l")[0];
                System.out.println("Running graph at " + path);
            } else {
                BPDFGui gui = new BPDFGui();
                gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gui.setSize (600,600);
                gui.setVisible (true);
            }
        } catch (ParseException exc) {
            System.err.println("Invalid arguments: " + exc);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BPDF", options);
        }
    }
}
