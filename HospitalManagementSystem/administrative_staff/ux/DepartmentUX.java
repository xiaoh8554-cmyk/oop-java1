package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DepartmentUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Department","Specialty","Head"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JTextField nameField=new JTextField(), specialtyField=new JTextField(), headField=new JTextField();
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton=new JButton("Create Department"), deleteButton=new JButton("Delete Selected"), refreshButton=new JButton("Refresh");
    public DepartmentUX(){setTitle("Departments / Specialties");setSize(800,500);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel f=new JPanel(new GridLayout(2,3,7,7));f.add(new JLabel("Department Name"));f.add(new JLabel("Specialty"));f.add(new JLabel("Head / Lead"));f.add(nameField);f.add(specialtyField);f.add(headField);JPanel s=new JPanel(new BorderLayout());s.add(f,BorderLayout.CENTER);JPanel b=new JPanel();b.add(backButton);b.add(addButton);b.add(deleteButton);b.add(refreshButton);s.add(b,BorderLayout.SOUTH);root.add(s,BorderLayout.SOUTH);setContentPane(root);}
}
