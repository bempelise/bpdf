package bpdf;

public class BpdfStatus {
    public BpdfStatus() {}
    public String path;
    public boolean gui = false;
    public boolean isSet = false;
    public boolean isConsistent = false;
    public boolean isLive = false;
    public boolean isSafe = false;

    void assign(BpdfStatus status) {
        path = status.path;
        gui = status.gui;
        isSet = status.isSet;
        isConsistent = status.isConsistent;
        isLive = status.isLive;
        isSafe = status.isSafe;
    }
}
