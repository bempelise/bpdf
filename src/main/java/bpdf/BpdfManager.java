package bpdf;

import bpdf.graph.BPDFGraph;
import bpdf.graph.BPDFGui;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BpdfManager {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public BpdfManager(BpdfStatus status) {
        m_status = status;
        loadGraph();
        if (m_status.gui) {
            launchgui();
        }
    }

    private void loadGraph() {
        if (m_status.path != null) {
            m_file = new File(m_status.path);
            if (m_file.isFile()) {
                m_status.isSet = true;
                m_graph = new BPDFGraph(m_file);
                m_status.isConsistent = m_graph.isConsistent();
                m_status.isLive = m_graph.isLive();
                m_status.isSafe = m_graph.isSafe();
            } else {
                LOG.warning("File " + m_status.path + " not found");
            }
        } else {
            LOG.warning("File path not set");
        }
    }

    private void launchgui() {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            LOG.severe(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            LOG.severe(e.getMessage());
        }
        catch (InstantiationException e) {
            LOG.severe(e.getMessage());
        }
        catch (IllegalAccessException e) {
            LOG.severe(e.getMessage());
        }

        m_gui = new BPDFGui();
        Thread t = new Thread(m_gui, "GUI");
        t.start();
    }

    private File m_file;
    private BPDFGraph m_graph;
    private BPDFGui m_gui;
    private BpdfStatus m_status = new BpdfStatus();
}
