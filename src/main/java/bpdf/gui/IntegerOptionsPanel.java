package bpdf.gui;

import bpdf.auxiliary.SpringUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;


public class IntegerOptionsPanel extends JPanel {
    private static final long serialVersionUID = 8543000000000001103L;
    private BPDFGui m_parent;
    private Map<String, Integer> m_values = new HashMap<String, Integer>();
    private List<ParameterPanel> m_params = new ArrayList<ParameterPanel>();

    IntegerOptionsPanel(BPDFGui parent) {
        super();
        m_parent = parent;
        setBorder(BorderFactory.createTitledBorder("Integer Parameters"));
        empty();
    }

    public Map<String, Integer> getParams() {
        Map<String, Integer> params = new HashMap<String, Integer>();
        for (ParameterPanel panel : m_params) {
            Pair<String, Integer> value = panel.getParam();
            if (value != null) {
                params.put(value.getKey(), value.getValue());
            } else {
                return null;
            }
        }
        return params;
    }

    private void empty() {
        removeAll();
        setLayout(new SpringLayout());
        JLabel label = new JLabel("No Parameters");
        label.setEnabled(false);
        add(label);
        SpringUtilities.makeCompactGrid(this, 1, 1,  // rows, cols
                                              5, 5,  // initX, initY
                                              5, 5); // xPad, yPad
    }


    public void refresh(Set<String> set) {
        removeAll();
        if (set.size() == 0) {
            empty();
        } else {
            setLayout(new SpringLayout());
            for (String param : set) {
                m_values.put(param, 1);
                m_params.add(new ParameterPanel(m_parent, param, "1"));
                add(m_params.get(m_params.size() - 1));
            }

            SpringUtilities.makeCompactGrid(this, set.size(), 1,    // rows, cols
                                                           5, 5,    // initX, initY
                                                           5, 5);   // xPad, yPad
        }
    }
}
