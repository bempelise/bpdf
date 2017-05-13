// Panel.java
package bpdf.graph;

import javax.swing.JPanel;

public class Panel extends JPanel {
    Panel() {
        parent_ = this;
    }

    Panel(Panel parent) {
        parent_ = parent;
    }

    public Panel getParent() {
        return parent_;
    }

    public void setParent(Panel parent) {
        parent_ = parent;
    }

    private Panel parent_;
}
