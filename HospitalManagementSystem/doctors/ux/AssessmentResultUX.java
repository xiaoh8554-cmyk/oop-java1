package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AssessmentResultUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Assessment ID","Patient","Doctor","Type","Date","Grade","Result","Lab Result","Bill RM","Status"},0) {
        public boolean isCellEditable(int r,int c){return false;}
    };
    protected final JTable table = new JTable(model);
    protected final JTextField patientField = new JTextField();
    protected final JComboBox<String> typeBox = new JComboBox<>();
    protected final JTextArea resultArea = new JTextArea(3,25);
    protected final JTextArea labArea = new JTextArea(3,25);
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton saveButton = new JButton("Save Assessment & Lab Result");
    protected final JButton refreshButton = new JButton("Refresh");

    public AssessmentResultUX(){
        setTitle("Assessment & Lab Results"); setSize(1180,700); setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root=new JPanel(new BorderLayout(10,10)); root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); root.add(new JScrollPane(table),BorderLayout.CENTER);
        JPanel form=new JPanel(new GridBagLayout()); GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(4,4,4,4);g.fill=GridBagConstraints.HORIZONTAL;
        g.gridx=0;g.gridy=0;form.add(new JLabel("Patient ID:"),g);g.gridx=1;form.add(patientField,g);g.gridx=2;form.add(new JLabel("Assessment Type:"),g);g.gridx=3;form.add(typeBox,g);
        g.gridx=0;g.gridy=1;g.anchor=GridBagConstraints.NORTH;form.add(new JLabel("Assessment Result:"),g);g.gridx=1;g.gridwidth=3;form.add(new JScrollPane(resultArea),g);
        g.gridx=0;g.gridy=2;g.gridwidth=1;form.add(new JLabel("Lab Result:"),g);g.gridx=1;g.gridwidth=3;form.add(new JScrollPane(labArea),g);
        JPanel buttons=new JPanel();buttons.add(backButton);buttons.add(saveButton);buttons.add(refreshButton);
        JPanel south=new JPanel(new BorderLayout());south.add(form,BorderLayout.CENTER);south.add(buttons,BorderLayout.SOUTH);root.add(south,BorderLayout.SOUTH);setContentPane(root);
    }
}
