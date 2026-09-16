package medical_manager.ux;

import javax.swing.*;
import java.awt.*;

public class ReportsUX extends JFrame {
    protected final JTextArea reportArea=new JTextArea();
    protected final JButton backButton=new JButton("\u2190 Back to Dashboard");
    protected final JButton refreshButton=new JButton("Generate / Refresh Report");
    public ReportsUX(){setTitle("Analytical Hospital Reports");setSize(780,620);setLocationRelativeTo(null);setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);reportArea.setEditable(false);reportArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));root.add(new JScrollPane(reportArea),BorderLayout.CENTER);JPanel buttons=new JPanel();buttons.add(backButton);buttons.add(refreshButton);root.add(buttons,BorderLayout.SOUTH);setContentPane(root);}
}
