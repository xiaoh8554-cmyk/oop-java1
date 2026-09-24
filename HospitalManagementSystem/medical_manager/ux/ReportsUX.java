package medical_manager.ux;

import java.awt.*;
import javax.swing.*;

public class ReportsUX extends JFrame {
    protected final JEditorPane reportArea = new JEditorPane();
    protected final JButton backButton = new JButton("← Back to Dashboard");
    protected final JButton refreshButton = new JButton("Generate / Refresh Report");

    public ReportsUX() {
        setTitle("Analytical Hospital Reports");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        reportArea.setEditable(false);
        reportArea.setContentType("text/html");
        reportArea.setBackground(new Color(248, 250, 252));
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        reportArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        reportArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(new Color(243, 246, 251));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("Analytical Hospital Report");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(17, 24, 39));
        header.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);
        header.add(buttonPanel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 228)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        scrollPane.getViewport().setBackground(new Color(248, 250, 252));

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        setContentPane(root);
    }
}
