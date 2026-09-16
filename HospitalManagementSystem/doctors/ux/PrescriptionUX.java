package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PrescriptionUX extends JFrame {
    protected final DefaultTableModel model=new DefaultTableModel(new String[]{"Prescription ID","Patient","Doctor","Date","Medicine","Instructions"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable table=new JTable(model);
    protected final JTextField patientField=new JTextField(), medicineField=new JTextField();
    protected final JTextArea instructionsArea=new JTextArea(4,35);
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton saveButton=new JButton("Issue Prescription"), refreshButton=new JButton("Refresh");
    public PrescriptionUX(){setTitle("Prescriptions");setSize(980,620);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));root.add(new JScrollPane(table),BorderLayout.CENTER);JPanel f=new JPanel(new GridBagLayout());GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(4,4,4,4);g.fill=GridBagConstraints.HORIZONTAL;g.gridx=0;g.gridy=0;f.add(new JLabel("Patient ID:"),g);g.gridx=1;f.add(patientField,g);g.gridx=0;g.gridy=1;f.add(new JLabel("Medicine:"),g);g.gridx=1;f.add(medicineField,g);g.gridx=0;g.gridy=2;g.anchor=GridBagConstraints.NORTH;f.add(new JLabel("Instructions:"),g);g.gridx=1;f.add(new JScrollPane(instructionsArea),g);JPanel b=new JPanel();b.add(backButton);b.add(saveButton);b.add(refreshButton);JPanel s=new JPanel(new BorderLayout());s.add(f,BorderLayout.CENTER);s.add(b,BorderLayout.SOUTH);root.add(s,BorderLayout.SOUTH);setContentPane(root);}
}
