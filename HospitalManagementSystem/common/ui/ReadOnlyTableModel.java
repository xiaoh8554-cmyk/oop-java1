package common.ui;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ReadOnlyTableModel extends DefaultTableModel {
    public ReadOnlyTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public ReadOnlyTableModel(Vector<?> columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
