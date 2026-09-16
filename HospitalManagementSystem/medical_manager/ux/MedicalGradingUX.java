package medical_manager.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedicalGradingUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"Assessment ID","Patient","Doctor","Type","Date","Grade","Result","Lab Result","Bill RM","Status"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JComboBox<String> gradeBox=new JComboBox<>(new String[]{"NORMAL","STABLE","OBSERVATION","HIGH_RISK","CRITICAL"});
    protected final JComboBox<String> statusBox=new JComboBox<>(new String[]{"COMPLETED","REVIEWED","FOLLOW_UP_REQUIRED","CLOSED"});
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton updateButton=new JButton("Update Grade / Status"), refreshButton=new JButton("Refresh");
    public MedicalGradingUX(){setTitle("Medical Grading");setSize(1180,560);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel b=new JPanel();b.add(backButton);b.add(new JLabel("Medical Grade:"));b.add(gradeBox);b.add(new JLabel("Status:"));b.add(statusBox);b.add(updateButton);b.add(refreshButton);root.add(b,BorderLayout.SOUTH);setContentPane(root);}
}
