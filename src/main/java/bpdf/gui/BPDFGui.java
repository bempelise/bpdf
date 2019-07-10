package bpdf.gui;

import bpdf.scheduling.NonSlottedScheduler;
import bpdf.scheduling.Scheduler;
import bpdf.scheduling.SlottedScheduler;
import bpdf.graph.DslParser;
import bpdf.graph.BPDFGraph;
import bpdf.graph.BPDFEdge;
import bpdf.graph.BPDFActor;
import bpdf.graph.FileHandler;
import bpdf.BpdfManager;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class BPDFGui extends JFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 8543000000000001001L;
    // main window
    private JPanel graphPanel;
    private IntegerOptionsPanel intPanel;
    private BooleanOptionsPanel boolPanel;
    private SchedulerOptionsPanel schedPanel;
    private GraphStatusPanel graphStatusPanel;
    private File currentFile;
    private BpdfManager m_manager;

    private mxGraph visGraph = new mxGraph();
    // private Map<String, Integer> intMap = new HashMap<String, Integer>();
    private Map<String, String> boolMap = new HashMap<String, String>();
    private Scheduler scheduler = new SlottedScheduler();

    public BPDFGui(BpdfManager manager) {
        super("Boolean Parametric Data Flow");
        m_manager = manager;
    }

    public void run() {
        Dimension minimumSize = new Dimension(10, 10);
        graphPanel = new JPanel();
        graphPanel.setMinimumSize(minimumSize);

        JPanel options = new JPanel();
        options.setMinimumSize(minimumSize);
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));

        intPanel = new IntegerOptionsPanel(this);
        boolPanel = new BooleanOptionsPanel(this);
        schedPanel = new SchedulerOptionsPanel(this);
        graphStatusPanel = new GraphStatusPanel();
        JButton analyse = new JButton("Analyse");
        analyse.setActionCommand("Analyse");
        analyse.addActionListener(this);
        JButton run = new JButton("Run");
        run.setActionCommand("Run");
        run.addActionListener(this);

        options.add(intPanel);
        options.add(boolPanel);
        options.add(schedPanel);
        options.add(graphStatusPanel);
        options.add(analyse);
        options.add(run);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, options, graphPanel);
        splitPane.setResizeWeight(0.1);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        MenuBar menuBar = new MenuBar(this);
        setJMenuBar(menuBar);

        // Make initialize and visible
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
        visualiseGraph();
    }

    private void visualiseGraph() {
        if (m_graph == null) {
            return;
        }

        visGraph = new mxGraph();

        Object parent = visGraph.getDefaultParent();
        visGraph.getModel().beginUpdate();
        try {
            List<mxCell> nodes = new ArrayList<mxCell>();
            List<BPDFActor> actorList = m_graph.getActors();
            for (BPDFActor actor : actorList) {
                Object v = visGraph.insertVertex(parent,
                                                 actor.getName(),
                                                 actor.getName(),
                                                 10, 10, 80, 30);
                nodes.add((mxCell) v);
            }

            List<BPDFEdge> edgeList = m_graph.getEdges();
            for (BPDFEdge edge : edgeList) {
                Object v1 = findNode(edge.getProducer().getName(), nodes);
                Object v2 = findNode(edge.getConsumer().getName(), nodes);
                visGraph.insertEdge(parent,edge.getName(), "", v1, v2);
            }

            mxCompactTreeLayout layout = new mxCompactTreeLayout(visGraph, false);
            layout.execute(visGraph.getDefaultParent());
        } finally {
            visGraph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(visGraph);
        graphPanel.removeAll();
        graphPanel.add(graphComponent);

        intPanel.refresh(m_graph.getIntParamSet());
        boolPanel.refresh(m_graph.getBoolParamSet());

        this.repaint();
        this.revalidate();
    }

    private Object findNode(String name, List<mxCell> celllist) {
        for (mxCell cell : celllist) {
            if (cell.getId().equals(name)) {
                return cell;
            }
        }
        System.out.println("Node " + name + " not found.");
        return null;
    }



/********************************************************************
 ** ACTION LISTENER
 ********************************************************************/

    public void actionPerformed(ActionEvent e) {
        boolean status = false;
        String command = e.getActionCommand();
        if (command.equals("Open")) {
            currentFile = openFile();
            if (currentFile != null) {
                m_graph = new BPDFGraph(new DslParser(currentFile));
                visualiseGraph();
            }
        } else if (command.equals("Save")) {
            saveFile();
        } else if (command.equals("Quit")) {
            dispose();
        } else if (command.equals("Analyse")) {
            if (m_graph != null) {
                m_graph.analyse();
            }
            graphStatusPanel.refresh(m_graph);
        } else if (command.equals("Run")) {
            if (currentFile != null) {
                BPDFGraph runGraph = new BPDFGraph(new DslParser(currentFile));
                Map<String, Integer> intMap = intPanel.getParams();
                Map<String, String> boolMap = boolPanel.getParams();

                switch (schedPanel.getScheduleType()) {
                    case SLOTTED:
                        scheduler = new SlottedScheduler(runGraph, intMap, boolMap);
                        break;
                    case NON_SLOTTED:
                        scheduler = new NonSlottedScheduler(runGraph, intMap, boolMap);
                        break;
                    default:
                    break;
                }

                int cycles = scheduler.getSchedule();

                System.out.println("Total time: " + cycles);
                System.out.println("");
            }
        }
    }

    private File openFile() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("BPDF Graph", "bpdf");
        // Makes all files default
        // fileChooser.addChoosableFileFilter(filter);
        // Makes .bpdf files default
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.CANCEL_OPTION) {
            return file;
        } else if (result == JFileChooser.APPROVE_OPTION) {
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
        fileChooser.setCurrentDirectory(new File("."));
        // fileChooser.setSelectedFile(file);
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        } else if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.exists()) {
                int response = JOptionPane.showConfirmDialog(null,
                    "Overwrite existing file?", "Confirm Overwrite",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            if (!FileHandler.writeFile(file, "")) {
                JOptionPane.showMessageDialog(null,
                    "IO error in saving file.", "File Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // private void printOptions() {
    //     Set<String> intParams = intMap.keySet();

    //     for (String s : intParams) {
    //         System.out.println("Integer " + s + " set to " + intMap.get(s));
    //     }

    //     Set<String> boolParams = boolMap.keySet();

    //     for (String s : boolParams) {
    //         System.out.println("Boolean " + s + " set to " + boolMap.get(s));
    //     }

    //     if (sched == 0) {
    //         System.out.println("Using a Slotted Scheduler");
    //     } else {
    //         System.out.println("Using a Non-Slotted Scheduler");
    //     }
    // }
}
