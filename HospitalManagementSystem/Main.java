import common.FileHandler;
import common.ui.LoginUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        FileHandler.ensureDataFiles();
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
