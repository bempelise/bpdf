package bpdf.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javafx.util.Pair;
import bpdf.auxiliary.SpringUtilities;
import javax.swing.SpringLayout;




public class ParameterPanel extends JPanel implements DocumentListener {
    private static final long serialVersionUID = 8543000000000001104L;
    private JTextField m_field;
    private JLabel m_label;
    private String m_value;
    private String m_name;
    private JFrame m_parent;

    ParameterPanel(JFrame parent, String name, String initValue) {
        m_name = name;
        m_parent = parent;
        m_value = initValue;
        m_label = new JLabel(name);
        m_field = new JTextField(initValue, 10);
        m_label.setLabelFor(m_field);
        m_field.setMaximumSize(m_field.getPreferredSize());
        m_field.getDocument().addDocumentListener(this);
        add(m_label);
        add(m_field);
        setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(this, 1, 2,  // rows, cols
                                              5, 5,  // initX, initY
                                              5, 5); // xPad, yPad
    }

    public Pair<String, Integer> getParam() {
        try {
            System.out.println("Value: " + m_value);
            return new Pair<String, Integer>(m_name, Integer.parseInt(m_value));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(m_parent, "Please enter an integer value",
                                                    "Illegal Integer Value",
                                                    JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }

    public void changedUpdate(DocumentEvent e) {
        m_value = m_field.getText();
    }

    public void removeUpdate(DocumentEvent e) {
        m_value = m_field.getText();
    }

    public void insertUpdate(DocumentEvent e) {
        m_value = m_field.getText();
    }
}
