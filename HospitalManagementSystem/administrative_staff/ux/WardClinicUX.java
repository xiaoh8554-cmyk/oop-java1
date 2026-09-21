package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WardClinicUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Name","Type","Location","Capacity"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton = new JButton("Create Ward / Clinic");
    protected final JButton deleteButton = new JButton("Delete Selected");
    protected final JButton refreshButton = new JButton("Refresh");

    public WardClinicUX() {
        setTitle("Wards / Clinics");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonBar.add(backButton);
        buttonBar.add(addButton);
        buttonBar.add(deleteButton);
        buttonBar.add(refreshButton);

        root.add(buttonBar, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
