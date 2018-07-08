// BPDFGui.java
package bpdf.graph;

import bpdf.auxiliary.SpringUtilities;
import bpdf.scheduling.*;

// util
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

// swing
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSplitPane;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.ButtonGroup;

// awt library
import java.awt.Container;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

// io
import java.io.File;

// mxGraph library
import com.mxgraph.view.mxGraph;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.model.mxCell;
import com.mxgraph.layout.mxCompactTreeLayout;

public class BPDFGui extends JFrame implements ActionListener
{
    // gui
    private static final long serialVersionUID = 1568196732681655874L;
    // menu
    private JMenu fileMenu;
    private JMenu analysisMenu;
    private JMenuBar menuBar;
    // main window
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel intPanel;
    private JPanel boolPanel;
    private JPanel schedPanel;
    private JButton run = new JButton("Run!");
    // settings
    private Dimension minimumSize = new Dimension(10, 10);
    private int sched = 0;
    private File currentFile;

    // content
    private BPDFGraph funcGraph = new BPDFGraph();
    private mxGraph visGraph = new mxGraph();
    private Map<String,Integer> intMap = new HashMap<String,Integer>();
    private Map<String,String> boolMap = new HashMap<String,String>();
    private Scheduler scheduler = new SlottedScheduler();

    BPDFGui()
    {
        super("Boolean Parametric Data Flow");

        // Integer Parameter Panel
        intPanel = new JPanel();
        intPanel.setBorder(
            BorderFactory.createTitledBorder("Integer Parameters"));
        makeEmptyPanel(intPanel);

        // Boolean Parameter Panel
        boolPanel = new JPanel();
        boolPanel.setBorder(
            BorderFactory.createTitledBorder("Boolean Parameters"));
        makeEmptyPanel(boolPanel);

        // Scheduler Panel
        schedPanel = new JPanel();
        schedPanel.setBorder(
            BorderFactory.createTitledBorder("Scheduler Options"));
        makeSchedPanel(schedPanel);

        // Run Button
        run.setActionCommand("run");
        run.addActionListener(this);

        // Left panel
        leftPanel = new JPanel();
        leftPanel.setMinimumSize(minimumSize);

        // Right panel
        rightPanel = new JPanel();
        rightPanel.setMinimumSize(minimumSize);
        rightPanel.setLayout(
            new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(intPanel);
        rightPanel.add(boolPanel);
        rightPanel.add(schedPanel);
        rightPanel.add(run);

        // Horizontal Splitbar
        splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.8);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // File menu
        fileMenu = new JMenu("File");
        fileMenu.add(makeMenuItem("Open"));
        fileMenu.add(makeMenuItem("Save"));
        fileMenu.add(makeMenuItem("Quit"));

        // Analysis menu
        analysisMenu = new JMenu("Analysis");
        analysisMenu.add(makeMenuItem("Consistency"));
        analysisMenu.add(makeMenuItem("Boundedness"));
        analysisMenu.add(makeMenuItem("Liveness"));

        // Menu bar
        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(analysisMenu);
        setJMenuBar (menuBar);
    }

/*******************************************************************
 ** GUI MAKERS
 *******************************************************************/

    private JMenuItem makeMenuItem (String name)
    {
        JMenuItem m = new JMenuItem (name);
        m.addActionListener (this);
        return m;
    }

    private JPanel makeEmptyPanel(JPanel panel)
    {
        panel.setLayout(new SpringLayout());
        JLabel label = new JLabel("No Parameters");
        label.setEnabled(false);
        panel.add(label);
        SpringUtilities.makeCompactGrid(panel,
            1, 1,               // rows, cols
            5, 5,               // initX, initY
            5, 5);              // xPad, yPad
        return panel;
    }

    private JPanel makeIntPanel(JPanel panel, Set<String> set)
    {
        panel.removeAll();

        if (set.size() == 0)
            panel = makeEmptyPanel(panel);
        else
        {
            SpringLayout layout = new SpringLayout();
            panel.setLayout(layout);

            for (String s : set)
            {
                // init value
                intMap.put(s,1);
                // label
                JLabel label = new JLabel(s);
                // textfield
                panel.add(label);
                final JTextField text = new JTextField("1",10);
                label.setLabelFor(text);
                text.setMaximumSize(text.getPreferredSize());
                text.getDocument().putProperty("param", s);
                text.getDocument().addDocumentListener(
                    new DocumentListener()
                {
                    public void changedUpdate(DocumentEvent e)
                    {
                        // updateInteger(e,text);
                    }
                    public void removeUpdate(DocumentEvent e)
                    {
                        updateInteger(e,text);
                    }
                    public void insertUpdate(DocumentEvent e)
                    {
                        updateInteger(e,text);
                    }
                });
                text.setActionCommand("int" + s);
                text.addActionListener(this);
                panel.add(text);
            }

            SpringUtilities.makeCompactGrid(panel,
                set.size(), 2,      // rows, cols
                5, 5,               // initX, initY
                5, 5);              // xPad, yPad
        }
        return panel;
    }

    private JPanel makeBoolPanel(JPanel panel, Set<String> set)
    {
        panel.removeAll();

        if (set.size() == 0)
            panel = makeEmptyPanel(panel);
        else
        {
            SpringLayout layout = new SpringLayout();
            panel.setLayout(layout);

            for (String s : set)
            {
                // init value
                boolMap.put(s,"*");
                // label
                JLabel label = new JLabel(s);
                panel.add(label);
                // textfield
                final JTextField text = new JTextField("*",10);
                label.setLabelFor(text);
                text.setMaximumSize(text.getPreferredSize());
                text.getDocument().putProperty("param", s);
                text.getDocument().addDocumentListener(
                    new DocumentListener()
                {
                    public void changedUpdate(DocumentEvent e)
                    {
                        // updateBoolean(e,text);
                    }
                    public void removeUpdate(DocumentEvent e)
                    {
                        updateBoolean(e,text);
                    }
                    public void insertUpdate(DocumentEvent e)
                    {
                        updateBoolean(e,text);
                    }
                });
                text.setActionCommand("bool" + s);
                text.addActionListener(this);
                panel.add(text);
            }

            SpringUtilities.makeCompactGrid(panel,
                set.size(), 2,      // rows, cols
                5, 5,               // initX, initY
                5, 5);              // xPad, yPad
        }
        return panel;
    }

    private JPanel makeSchedPanel(JPanel panel)
    {
        String schedOptions[] = {"slotted","non slotted"};
        JComboBox cbSched = new JComboBox(schedOptions);
        cbSched.setEditable(false);
        cbSched.setMaximumSize(cbSched.getPreferredSize());
        cbSched.setActionCommand("schedSelect");
        cbSched.addActionListener(this);
        panel.add(cbSched);
        actionPerformed(new ActionEvent(cbSched,
            ActionEvent.ACTION_PERFORMED,"schedSelect"));
        return panel;
    }

    private JPanel makeBoolValuePanel(JPanel panel)
    {
        JButton gen = new JButton("Generate");
        gen.setActionCommand("generate");
        gen.addActionListener(this);
        JButton save = new JButton("Save");
        save.setActionCommand("saveBool");
        save.addActionListener(this);
        JButton load = new JButton("Load");
        load.setActionCommand("loadBool");
        load.addActionListener(this);
        panel.add(gen);
        panel.add(save);
        panel.add(load);
        return panel;
    }

    private void makeGraph(File file)
    {
        visGraph = new mxGraph();
        funcGraph = new BPDFGraph(file);

        funcGraph.verifyGraph();
        Object parent = visGraph.getDefaultParent();

        visGraph.getModel().beginUpdate();
        try
        {
            List<mxCell> nodes = new ArrayList<mxCell>();

            List<BPDFActor> actorList = funcGraph.getActors();
            for (BPDFActor actor : actorList)
            {
                Object v = visGraph.insertVertex(parent, actor.getName(),actor.getName(), 10, 10, 80, 30);
                nodes.add((mxCell) v);
            }

            List<BPDFEdge> edgeList = funcGraph.getEdges();
            for (BPDFEdge edge : edgeList)
            {
                Object v1 = findNode(edge.getProducer().getName(),nodes);
                Object v2 = findNode(edge.getConsumer().getName(),nodes);
                visGraph.insertEdge(parent,edge.getName(),"",v1,v2);
            }

            mxCompactTreeLayout layout
                = new mxCompactTreeLayout(visGraph, false);
            layout.execute(visGraph.getDefaultParent());
        }
        finally
        {
            visGraph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent
            = new mxGraphComponent(visGraph);
        leftPanel.removeAll();
        leftPanel.add(graphComponent);

        makeIntPanel(intPanel, funcGraph.getIntParamSet());
        makeBoolPanel(boolPanel, funcGraph.getBoolParamSet());

        this.repaint();
        this.revalidate();
    }

    private Object findNode(String name, List<mxCell> celllist)
    {
        for (mxCell cell : celllist)
        {
            if (cell.getId().equals(name)) return(cell);
        }
        System.out.println("Node " + name + " not found.");
        return null;
    }



/********************************************************************
 ** GRAPH ACTIONS
 ********************************************************************/

    private boolean consCheck(boolean msg)
    {
        if (!funcGraph.isConsistent())
        {
            JOptionPane.showMessageDialog(((JFrame) this),
            "Graph is inconsistent.",
            "Consistency Analysis",
            JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if (msg)
        {
            JOptionPane.showMessageDialog(((JFrame) this),
            "Graph is consistent.",
            "Consistency analysis",
            JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    private boolean boundCheck(boolean msg)
    {
        if (!funcGraph.isSafe())
        {
            JOptionPane.showMessageDialog(((JFrame) this),
            "Graph is not safe.",
            "Boundedness Analysis",
            JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if (msg)
        {
            JOptionPane.showMessageDialog(((JFrame) this),
            "Graph is safe.",
            "Boundedness analysis",
            JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    private boolean liveCheck(boolean msg)
    {
        if (!funcGraph.isLive())
        {
            JOptionPane.showMessageDialog(((JFrame) this),
            "Graph is not live.",
            "Liveness Analysis",
            JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if (msg)
        {
            JOptionPane.showMessageDialog(((JFrame) this),
            "Graph is live.",
            "Liveness analysis",
            JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    private void updateInteger(DocumentEvent event, JTextField text)
    {
        if (text.getText().length() > 0)
        {
            try
            {
                int value = Integer.parseInt(text.getText());
                String param = (String)
                    event.getDocument().getProperty("param");
                intMap.put(param,value);
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(((JFrame) this),
                "Please enter an integer value",
                "Illegal Integer Value",
                JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateBoolean(DocumentEvent event, JTextField text)
    {
        String value = text.getText();
        String param = (String)
            event.getDocument().getProperty("param");

        if (text.getText().length() > 0)
        {
            // if (value.charAt(0)=='*')
            //     boolMap.put(param,"*");
            // else
            if (value.matches("[*01]+"))
                boolMap.put(param,value);
            else
            {
                JOptionPane.showMessageDialog(((JFrame) this),
                "Use 0 (false), 1(true) or * (random)",
                "Invalid Boolean Value",
                JOptionPane.WARNING_MESSAGE);
            }
        }
    }

/********************************************************************
 ** ACTION LISTENER
 ********************************************************************/

    public void actionPerformed (ActionEvent e)
    {
        boolean status = false;
        String command = e.getActionCommand();
        // Menu Bar "File"
        if (command.equals("Open"))
        {
            currentFile = openFile();
            if (currentFile != null) makeGraph(currentFile);
        }
        else if (command.equals("Save"))
        {
            saveFile();
        }
        else if (command.equals("Quit"))
        {
            dispose();
        }
        // Menu Bar "Analysis"
        else if (command.equals("Consistency"))
        {
            if (currentFile != null)
                consCheck(true);
        }
        else if (command.equals("Boundedness"))
        {
            if (currentFile != null)
                boundCheck(true);
        }
        else if (command.equals("Liveness"))
        {
            if (currentFile != null)
                liveCheck(true);
        }
        // Parameter Set Up
        else if (command.startsWith("bool"))
        {
            // Correct values
            String param = command.substring(4,command.length());
            ((JTextField) e.getSource()).setText(boolMap.get(param));
        }
        else if (command.startsWith("int"))
        {
            // Correct values
            String param = command.substring(3,command.length());
            ((JTextField) e.getSource())
                .setText(String.valueOf(intMap.get(param)));
        }
        // Scheduler Set Up
        else if (command.equals("schedSelect"))
        {
            sched = ((JComboBox)e.getSource()).getSelectedIndex();
        }
        else if (command.equals("run"))
        {
            if (currentFile != null)
            {
                BPDFGraph runGraph = new BPDFGraph(currentFile);
                runGraph.verifyGraph();

                if (sched == 0)
                    scheduler = new SlottedScheduler(
                        runGraph,intMap,boolMap);
                else
                    scheduler = new NonSlottedScheduler(
                        runGraph,intMap,boolMap);

                int cycles = scheduler.getSchedule();

                System.out.println("Total time: " + cycles);
                System.out.println("");
            }
        }
    }

/***********************************************************************
 ** FILE ACTIONS
 ***********************************************************************/

    private File openFile()
    {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("BPDF Graph","bpdf");
        // Makes all files default
        // fileChooser.addChoosableFileFilter(filter);
        // Makes .bpdf files default
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new File ("."));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.CANCEL_OPTION){
            return file;
        } else if (result == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
        } else {
            JOptionPane.showMessageDialog(null,
                "IO error in saving file.", "File Save Error",
                JOptionPane.ERROR_MESSAGE);
            return file;
        }
        return file;
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory (new File ("."));
        // fileChooser.setSelectedFile(file);
        int result = fileChooser.showSaveDialog (this);

        if (result == JFileChooser.CANCEL_OPTION)
            return;
        else if (result == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            if (file.exists())
            {
                int response = JOptionPane.showConfirmDialog (null,
                    "Overwrite existing file?","Confirm Overwrite",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION)
                    return;
            }
            if (!FileHandler.writeFile(file,""))
                JOptionPane.showMessageDialog(null,
                    "IO error in saving file.", "File Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printOptions() {
        Set<String> intParams = intMap.keySet();

        for (String s : intParams) {
            System.out.println("Integer " + s + " set to " + intMap.get(s));
        }

        Set<String> boolParams = boolMap.keySet();

        for (String s : boolParams) {
            System.out.println("Boolean " + s + " set to " + boolMap.get(s));
        }

        if (sched == 0) System.out.println("Using a Slotted Scheduler");
        else            System.out.println("Using a Non-Slotted Scheduler");
    }
}




