package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BillingUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"Bill ID","Patient ID","Assessment ID","Amount (RM)","Status","Date"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JTextField patientField=new JTextField(), assessmentField=new JTextField(), amountField=new JTextField();
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton=new JButton("Create Bill"), paidButton=new JButton("Mark Paid"), deleteButton=new JButton("Delete Bill"), refreshButton=new JButton("Refresh");
    public BillingUX(){setTitle("Billing Management");setSize(900,530);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel f=new JPanel(new GridLayout(2,3,7,7));f.add(new JLabel("Patient ID"));f.add(new JLabel("Assessment ID (optional)"));f.add(new JLabel("Amount RM"));f.add(patientField);f.add(assessmentField);f.add(amountField);JPanel s=new JPanel(new BorderLayout());s.add(f,BorderLayout.CENTER);JPanel b=new JPanel();b.add(backButton);b.add(addButton);b.add(paidButton);b.add(deleteButton);b.add(refreshButton);s.add(b,BorderLayout.SOUTH);root.add(s,BorderLayout.SOUTH);setContentPane(root);}
}
