package bpdf.gui;

import bpdf.graph.BPDFGraph;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class GraphStatusPanel extends JPanel {
    private static final long serialVersionUID = 8543000000000001108L;
    private JLabel m_consistent;
    private JLabel m_liveness;
    private JLabel m_safety;

    GraphStatusPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Graph Status"));
        setLayout(new GridLayout(3, 1));
        m_consistent = new JLabel("Consistency");
        m_liveness = new JLabel("Liveness");
        m_safety = new JLabel("Period Safety");
        add(m_consistent);
        add(m_liveness);
        add(m_safety);
        refresh(null);
    }

    public void refresh(BPDFGraph graph) {
            if (graph == null) {
                m_consistent.setEnabled(false);
                m_liveness.setEnabled(false);
                m_safety.setEnabled(false);
            } else {
                m_consistent.setEnabled(graph.isConsistent());
                m_liveness.setEnabled(graph.isLive());
                m_safety.setEnabled(graph.isSafe());
            }
    }
}
