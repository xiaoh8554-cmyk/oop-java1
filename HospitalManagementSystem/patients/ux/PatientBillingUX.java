package patients.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientBillingUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"Bill ID","Patient ID","Assessment ID","Amount RM","Status","Date"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JLabel totalLabel=new JLabel("Outstanding: RM 0.00");
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton payButton=new JButton("Pay Selected Bill"), refreshButton=new JButton("Refresh");
    public PatientBillingUX(){setTitle("My Billing");setSize(900,520);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel s=new JPanel(new BorderLayout());s.add(totalLabel,BorderLayout.WEST);JPanel b=new JPanel();b.add(backButton);b.add(payButton);b.add(refreshButton);s.add(b,BorderLayout.EAST);root.add(s,BorderLayout.SOUTH);setContentPane(root);}
}
