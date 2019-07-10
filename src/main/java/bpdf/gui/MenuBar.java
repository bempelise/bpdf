package bpdf.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;



public class MenuBar extends JMenuBar {
    private static final long serialVersionUID = 8543000000000001107L;
    private ActionListener m_parent;

    MenuBar(ActionListener parent) {
        m_parent = parent;

        JMenu fileMenu = new JMenu("File");
        addMenuItem("Open", fileMenu);
        addMenuItem("Save", fileMenu);
        addMenuItem("Quit", fileMenu);

        JMenu graphMenu = new JMenu("Graph");
        addMenuItem("Analyse", graphMenu);

        add(fileMenu);
        add(graphMenu);
    }

    private void addMenuItem(String name, JMenu menu) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(m_parent);
        menu.add(item);
    }
}
