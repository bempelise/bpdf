package bpdf.gui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class BooleanOptionsPanel extends OptionsPanel<String> {
    private static final long serialVersionUID = 8543000000000001105L;

    BooleanOptionsPanel(JFrame parent) {
        super(parent, "Boolean Parameters");
    }

    protected Map<String, String> convertParams(Map<String, String> stringParams) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : stringParams.entrySet()) {
            if (entry.getValue().matches("[*01]+")) {
                params.put(entry.getKey(), entry.getValue());
            } else {
                JOptionPane.showMessageDialog(m_parent,
                                              "Use 0 (false), 1(true) or * (random)",
                                              "Invalid Boolean Value",
                                              JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        return params;
    }
}
