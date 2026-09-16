package administrative_staff.ui;

import administrative_staff.ux.WardClinicUX;
import common.*;
import javax.swing.*;
import java.util.*;

public class WardClinicUI extends WardClinicUX {
    public WardClinicUI(){super(); backButton.addActionListener(e -> dispose());refresh();addButton.addActionListener(e->add());deleteButton.addActionListener(e->del());refreshButton.addActionListener(e->refresh());}
    private void refresh(){model.setRowCount(0);for(String[]r:FileHandler.read(FileHandler.WARDS))if(r.length>=5)model.addRow(r);}
    private void add(){String n=nameField.getText().trim(),loc=locationField.getText().trim(),cap=capacityField.getText().trim();if(n.isEmpty()||loc.isEmpty()||cap.isEmpty()){JOptionPane.showMessageDialog(this,"Complete all fields.");return;}if(DataUtil.toInt(cap,-1)<1){JOptionPane.showMessageDialog(this,"Capacity must be a positive number.");return;}String id=FileHandler.nextId(FileHandler.WARDS,"W",3);FileHandler.append(FileHandler.WARDS,new String[]{id,n,typeBox.getSelectedItem().toString(),loc,cap});nameField.setText("");locationField.setText("");capacityField.setText("");refresh();}
    private void del(){int i=table.getSelectedRow();if(i<0){JOptionPane.showMessageDialog(this,"Select a row.");return;}String id=model.getValueAt(i,0).toString();List<String[]>rows=new ArrayList<>(FileHandler.read(FileHandler.WARDS));rows.removeIf(r->r.length>0&&r[0].equals(id));FileHandler.writeAll(FileHandler.WARDS,rows);refresh();}
}
