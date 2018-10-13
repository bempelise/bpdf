package bpdf;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.logging.Logger;
import java.util.logging.Level;

import bpdf.graph.BPDFGraph;

public class BpdfManager {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public BpdfManager(BpdfStatus status) {
        LOGGER.setLevel(Level.INFO);
        _status = status;
        loadGraph();
    }

    public void run() {

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
                LOGGER.warning("File " + _status.path + " not found");
            }
        } else {
            LOGGER.warning("File path not set.");
        }
    }

    private File _file;
    private BPDFGraph _graph;
    private BpdfStatus _status;
}
