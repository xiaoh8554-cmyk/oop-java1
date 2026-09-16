package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WardClinicUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Name","Type","Location","Capacity"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JTextField nameField=new JTextField();
    protected final JComboBox<String> typeBox=new JComboBox<>(new String[]{"WARD","CLINIC"});
    protected final JTextField locationField=new JTextField();
    protected final JTextField capacityField=new JTextField();
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton=new JButton("Create Ward / Clinic"), deleteButton=new JButton("Delete Selected"), refreshButton=new JButton("Refresh");
    public WardClinicUX(){
        setTitle("Wards / Clinics"); setSize(820,520); setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root=new JPanel(new BorderLayout(10,10)); root.setBorder(BorderFactory.createEmptyBorder(14,14,14,14)); root.add(new JScrollPane(table),BorderLayout.CENTER);
        JPanel form=new JPanel(new GridLayout(2,4,7,7)); String[] l={"Name","Type","Location","Capacity"}; for(String x:l)form.add(new JLabel(x)); form.add(nameField);form.add(typeBox);form.add(locationField);form.add(capacityField);
        JPanel south=new JPanel(new BorderLayout());south.add(form,BorderLayout.CENTER); JPanel b=new JPanel();b.add(backButton);b.add(addButton);b.add(deleteButton);b.add(refreshButton);south.add(b,BorderLayout.SOUTH);root.add(south,BorderLayout.SOUTH);setContentPane(root);
    }
}
