package bpdf.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class SchedulerOptionsPanel extends JPanel implements ActionListener {
    public enum ScheduleType {
        SLOTTED {
            public String toString() {
                return "Slotted Scheduler";
            }
        },
        NON_SLOTTED {
            public String toString() {
                return "Non-Slotted Scheduler";
            }
        }
    }

    private static final long serialVersionUID = 8543000000000001102L;
    private BPDFGui m_parent;
    private ScheduleType m_scheduleType = ScheduleType.SLOTTED;

    SchedulerOptionsPanel(BPDFGui parent) {
        super();
        m_parent = parent;
        setBorder(BorderFactory.createTitledBorder("Scheduler Options"));

        JComboBox<ScheduleType> box = new JComboBox<>(ScheduleType.values());
        box.setEditable(false);
        box.setMaximumSize(box.getPreferredSize());
        box.setActionCommand("selectScheduleType");
        box.addActionListener(this);
        add(box);
        actionPerformed(new ActionEvent(box, ActionEvent.ACTION_PERFORMED, "selectScheduleType"));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("selectScheduleType")) {
            m_scheduleType = (ScheduleType)((JComboBox) e.getSource()).getSelectedItem();
        } else {
            m_parent.actionPerformed(e);
        }
    }

    public ScheduleType getScheduleType() {
        return m_scheduleType;
    }
}
