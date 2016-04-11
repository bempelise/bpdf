// BPDFMain.java
package bpdf.graph;

import javax.swing.JFrame;

public class BPDFMain
{   
    public static void main(String[] args)
    {
        BPDFGui gui = new BPDFGui();

        gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gui.setSize (600,600);
        gui.setVisible (true);

        // String path = "vc1coarse";
        // RunGraph rg = new RunGraph(2,2,8);
        // rg.runAll(path);
    }
}