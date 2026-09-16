package administrative_staff.ui;

import administrative_staff.ux.DepartmentUX;
import common.FileHandler;
import javax.swing.*;
import java.util.*;

public class DepartmentUI extends DepartmentUX {
    public DepartmentUI(){super(); backButton.addActionListener(e -> dispose());refresh();addButton.addActionListener(e->add());deleteButton.addActionListener(e->del());refreshButton.addActionListener(e->refresh());}
    private void refresh(){model.setRowCount(0);for(String[]r:FileHandler.read(FileHandler.DEPARTMENTS))if(r.length>=4)model.addRow(r);}
    private void add(){String n=nameField.getText().trim(),s=specialtyField.getText().trim(),h=headField.getText().trim();if(n.isEmpty()||s.isEmpty()){JOptionPane.showMessageDialog(this,"Department and specialty are required.");return;}String id=FileHandler.nextId(FileHandler.DEPARTMENTS,"DEP",3);FileHandler.append(FileHandler.DEPARTMENTS,new String[]{id,n,s,h});nameField.setText("");specialtyField.setText("");headField.setText("");refresh();}
    private void del(){int i=table.getSelectedRow();if(i<0){JOptionPane.showMessageDialog(this,"Select a row.");return;}String id=model.getValueAt(i,0).toString();List<String[]>rows=new ArrayList<>(FileHandler.read(FileHandler.DEPARTMENTS));rows.removeIf(r->r.length>0&&r[0].equals(id));FileHandler.writeAll(FileHandler.DEPARTMENTS,rows);refresh();}
}
