package patients.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedicalRecordUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"Assessment ID","Patient","Doctor","Type","Date","Grade","Result","Lab Result","Bill RM","Status"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton refreshButton=new JButton("Refresh My Records");
    public MedicalRecordUX(){setTitle("My Medical Records");setSize(1180,560);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel buttons=new JPanel();buttons.add(backButton);buttons.add(refreshButton);root.add(buttons,BorderLayout.SOUTH);setContentPane(root);}
}
