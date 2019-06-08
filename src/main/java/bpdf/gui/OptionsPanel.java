package bpdf.gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public abstract class OptionsPanel<T> extends JPanel {
    private static final long serialVersionUID = 8543000000000001106L;
    protected JFrame m_parent;
    protected List<ParameterPanel> m_params = new ArrayList<ParameterPanel>();

    OptionsPanel(JFrame parent, String title) {
        super();
        m_parent = parent;
        setBorder(BorderFactory.createTitledBorder(title));
        refresh(null);
    }

    public void refresh(Set<String> set) {
        int size = set != null ? set.size() : 0;

        removeAll();
        setLayout(new GridLayout(size, 1));

        if (size == 0) {
            JLabel label = new JLabel("No Parameters");
            label.setEnabled(false);
            add(label);
        } else {
            for (String param : set) {
                m_params.add(new ParameterPanel(m_parent, param, "1"));
                add(m_params.get(m_params.size() - 1));
            }
        }
    }

    public Map<String, T> getParams() {
        Map<String, String> stringParams = new HashMap<String, String>();
        for (ParameterPanel panel : m_params) {
            Pair<String, String> value = panel.getParam();
            stringParams.put(value.getKey(), value.getValue());
        }
        Map<String, T> params = convertParams(stringParams);
        return params;
    }

    protected abstract Map<String, T> convertParams(Map<String, String> params);
}
