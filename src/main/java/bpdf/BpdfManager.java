package bpdf;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.logging.Logger;
import java.util.logging.Level;

import bpdf.graph.BPDFGraph;
import bpdf.graph.BPDFGui;

public class BpdfManager {
    private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    final BpdfStatus _status = new BpdfStatus();

    public BpdfManager(BpdfStatus status) {
        _status.assign(status);
        loadGraph();
        if (_status.gui) {
            launchgui();
        }
    }

    private void loadGraph() {
        if (_status.path != null) {
            _file = new File(_status.path);
            if (_file.isFile()) {
                _status.isSet = true;
                _graph = new BPDFGraph(_file);
                _status.isConsistent = _graph.isConsistent();
                _status.isLive = _graph.isLive();
                _status.isSafe = _graph.isSafe();
            } else {
                LOG.warning("File " + _status.path + " not found");
            }
        } else {
            LOG.warning("File path not set");
        }
    }

    private void launchgui() {
        _gui = new BPDFGui();
        Thread t = new Thread(_gui, "GUI");
        t.start();
    }

    private File _file;
    private BPDFGraph _graph;
    private BPDFGui _gui;
}
