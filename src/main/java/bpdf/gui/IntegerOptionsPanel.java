package bpdf.gui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class IntegerOptionsPanel extends OptionsPanel<Integer> {
    private static final long serialVersionUID = 8543000000000001103L;

    IntegerOptionsPanel(JFrame parent) {
        super(parent, "Integer Parameters");
    }

    protected Map<String, Integer> convertParams(Map<String, String> stringParams) {
        Map<String, Integer> params = new HashMap<String, Integer>();
        for (Map.Entry<String, String> entry : stringParams.entrySet()) {
            try {
                int value = Integer.parseInt(entry.getValue());
                params.put(entry.getKey(), value);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(m_parent, "Please enter an integer value",
                                                        "Illegal Integer Value",
                                                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        return params;
    }

}
