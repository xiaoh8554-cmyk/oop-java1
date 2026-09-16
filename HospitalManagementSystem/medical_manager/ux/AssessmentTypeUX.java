package medical_manager.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AssessmentTypeUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Assessment Type","Description","Base Fee (RM)"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JTextField nameField=new JTextField(), descriptionField=new JTextField(), feeField=new JTextField();
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton=new JButton("Create Assessment Type"), deleteButton=new JButton("Delete Selected"), refreshButton=new JButton("Refresh");
    public AssessmentTypeUX(){setTitle("Assessment / Check-up Types");setSize(900,520);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel f=new JPanel(new GridLayout(2,3,7,7));f.add(new JLabel("Name"));f.add(new JLabel("Description"));f.add(new JLabel("Base Fee RM"));f.add(nameField);f.add(descriptionField);f.add(feeField);JPanel s=new JPanel(new BorderLayout());s.add(f,BorderLayout.CENTER);JPanel b=new JPanel();b.add(backButton);b.add(addButton);b.add(deleteButton);b.add(refreshButton);s.add(b,BorderLayout.SOUTH);root.add(s,BorderLayout.SOUTH);setContentPane(root);}
}
