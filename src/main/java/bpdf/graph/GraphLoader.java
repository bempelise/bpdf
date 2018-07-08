// GraphLoader.java
package bpdf.graph;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

public class GraphLoader {
    GraphLoader() {}

    BPDFGraph openFile(File file) {
        return null;
    }

    BPDFGraph openFile(String filename) {
        File file;
        try
        {
            file = new File(filename);

            //if file doesnt exists, then create it
            if(!file.exists())
            {
                file.createNewFile();
            }
        }
        catch(IOException e)
        {
            // "Failed to open BPDF graph at " + filename
            e.printStackTrace();
        }
        // if (file)
        // {
        //     return openFile(file);
        // } 
        return null;
    }
}
