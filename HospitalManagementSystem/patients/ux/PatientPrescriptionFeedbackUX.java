package patients.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientPrescriptionFeedbackUX extends JFrame {
    protected final DefaultTableModel prescriptionModel=new DefaultTableModel(new String[]{"Prescription ID","Patient","Doctor","Date","Medicine","Instructions"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final DefaultTableModel feedbackModel=new DefaultTableModel(new String[]{"Feedback ID","Patient","Doctor","Date","Type","Message"},0){public boolean isCellEditable(int r,int c){return false;}};
    protected final JTable prescriptionTable=new JTable(prescriptionModel), feedbackTable=new JTable(feedbackModel);
    protected final JTextField doctorField=new JTextField(12);
    protected final JTextArea feedbackArea=new JTextArea(4,35);
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton submitButton=new JButton("Submit Patient Feedback"), refreshButton=new JButton("Refresh");
    public PatientPrescriptionFeedbackUX(){setTitle("Prescriptions & Feedback");setSize(1000,700);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);JTabbedPane tabs=new JTabbedPane();tabs.addTab("My Prescriptions",new JScrollPane(prescriptionTable));JPanel feedbackPanel=new JPanel(new BorderLayout(8,8));feedbackPanel.add(new JScrollPane(feedbackTable),BorderLayout.CENTER);JPanel form=new JPanel(new GridBagLayout());GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(4,4,4,4);g.fill=GridBagConstraints.HORIZONTAL;g.gridx=0;g.gridy=0;form.add(new JLabel("Doctor ID:"),g);g.gridx=1;form.add(doctorField,g);g.gridx=0;g.gridy=1;g.anchor=GridBagConstraints.NORTH;form.add(new JLabel("Your Feedback:"),g);g.gridx=1;form.add(new JScrollPane(feedbackArea),g);JPanel b=new JPanel();b.add(backButton);b.add(submitButton);b.add(refreshButton);JPanel south=new JPanel(new BorderLayout());south.add(form,BorderLayout.CENTER);south.add(b,BorderLayout.SOUTH);feedbackPanel.add(south,BorderLayout.SOUTH);tabs.addTab("Clinical / Patient Feedback",feedbackPanel);setContentPane(tabs);}
}
