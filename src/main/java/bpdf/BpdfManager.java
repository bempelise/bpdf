package bpdf;

import bpdf.graph.BPDFGraph;
import bpdf.graph.DslParser;
import bpdf.gui.BPDFGui;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BpdfManager {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public BpdfManager(BpdfStatus status) {
        m_status = status;
        if (m_status.gui) {
            launchgui();
        }
        loadGraph(m_status.path);
    }


    private String loadGraph(String path) {
        m_status.path = path;
        String message;
        if (m_status.path != null) {
            File file = new File(m_status.path);
            if (file.isFile()) {
                m_status.isSet = true;
                m_graph = new BPDFGraph(new DslParser(file));
            } else {
                message = "File " + m_status.path + " not found";
                LOG.warning(message);
            }
        } else {
            message = "File path not set"
            LOG.warning(message);
        }
        return message;
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
        m_gui = new BPDFGui(this);
        Thread t = new Thread(m_gui, "GUI");
        t.start();
    }

    private BPDFGraph m_graph;
    private BPDFGui m_gui;
    private BpdfStatus m_status = new BpdfStatus();
}
