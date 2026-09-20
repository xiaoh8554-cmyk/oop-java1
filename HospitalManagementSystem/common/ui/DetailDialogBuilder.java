package common.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DetailDialogBuilder {
    private final Component parent;
    private final String windowTitle;
    private int width = 680;
    private int height = 520;

    private String headerTitle = "APU MEDICAL CENTRE - RECORD DETAIL";
    private String headerSubtitle = "";
    private Color headerSubtitleColor = new Color(20, 60, 140);

    private final JPanel bodyPanel = new JPanel(new GridBagLayout());
    private int currentRow = 0;

    private final List<JButton> customActionButtons = new ArrayList<>();
    private JDialog activeDialog;

    public DetailDialogBuilder(Component parent, String windowTitle) {
        this.parent = parent;
        this.windowTitle = windowTitle;
        this.bodyPanel.setBorder(BorderFactory.createTitledBorder("Information & Clinical Reference"));
    }

    public DetailDialogBuilder setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public DetailDialogBuilder setHeader(String title, String subtitle) {
        return setHeader(title, subtitle, new Color(20, 60, 140));
    }

    public DetailDialogBuilder setHeader(String title, String subtitle, Color subtitleColor) {
        this.headerTitle = title;
        this.headerSubtitle = subtitle;
        this.headerSubtitleColor = subtitleColor != null ? subtitleColor : new Color(20, 60, 140);
        return this;
    }

    public DetailDialogBuilder setBorderTitle(String borderTitle) {
        this.bodyPanel.setBorder(BorderFactory.createTitledBorder(borderTitle));
        return this;
    }

    public DetailDialogBuilder addField(String label, String value) {
        GridBagConstraints g = createConstraints(false);
        g.gridx = 0; g.gridy = currentRow;
        bodyPanel.add(new JLabel(label), g);

        g.gridx = 1;
        bodyPanel.add(new JLabel(value != null ? value : "N/A"), g);
        currentRow++;
        return this;
    }

    public DetailDialogBuilder addHighlightField(String label, String value, Color color) {
        GridBagConstraints g = createConstraints(false);
        g.gridx = 0; g.gridy = currentRow;
        bodyPanel.add(new JLabel(label), g);

        g.gridx = 1;
        JLabel valLabel = new JLabel(value != null ? value : "N/A");
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        valLabel.setForeground(color != null ? color : new Color(20, 60, 140));
        bodyPanel.add(valLabel, g);
        currentRow++;
        return this;
    }

    public DetailDialogBuilder addTextAreaField(String label, String text, int rows) {
        GridBagConstraints g = createConstraints(true);
        g.gridx = 0; g.gridy = currentRow;
        bodyPanel.add(new JLabel(label), g);

        g.gridx = 1;
        JTextArea area = new JTextArea(text != null ? text : "None", rows, 32);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBackground(new Color(250, 250, 250));
        area.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        bodyPanel.add(new JScrollPane(area), g);
        currentRow++;
        return this;
    }

    public DetailDialogBuilder addActionButton(JButton button) {
        if (button != null) {
            customActionButtons.add(button);
        }
        return this;
    }

    public void dispose() {
        if (activeDialog != null) {
            activeDialog.dispose();
        }
    }

    public JDialog show() {
        activeDialog = new JDialog(SwingUtilities.getWindowAncestor(parent), windowTitle, Dialog.ModalityType.APPLICATION_MODAL);
        activeDialog.setSize(width, height);
        activeDialog.setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 245), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        header.setBackground(new Color(245, 248, 255));

        JLabel titleLbl = new JLabel(headerTitle);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLbl.setForeground(new Color(20, 60, 140));

        JLabel subLbl = new JLabel(headerSubtitle);
        subLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        subLbl.setForeground(headerSubtitleColor);

        header.add(titleLbl);
        header.add(subLbl);
        content.add(header, BorderLayout.NORTH);

        content.add(bodyPanel, BorderLayout.CENTER);

        // Footer Actions
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        for (JButton btn : customActionButtons) {
            south.add(btn);
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> activeDialog.dispose());
        south.add(closeBtn);

        content.add(south, BorderLayout.SOUTH);

        activeDialog.setContentPane(content);
        activeDialog.setVisible(true);
        return activeDialog;
    }

    private GridBagConstraints createConstraints(boolean topAnchor) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.anchor = topAnchor ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }
}
