package administrative_staff.ui;

import administrative_staff.ux.BillingUX;
import common.*;
import javax.swing.*;
import java.util.*;

public class BillingUI extends BillingUX {
    public BillingUI(){super(); backButton.addActionListener(e -> dispose());refresh();addButton.addActionListener(e->add());paidButton.addActionListener(e->markPaid());deleteButton.addActionListener(e->del());refreshButton.addActionListener(e->refresh());}
    private void refresh(){model.setRowCount(0);for(String[]r:FileHandler.read(FileHandler.BILLING))if(r.length>=6)model.addRow(r);}
    private void add(){String p=patientField.getText().trim(),a=assessmentField.getText().trim(),amt=amountField.getText().trim();if(!AuthService.userIdExists(p,"PATIENT")){JOptionPane.showMessageDialog(this,"Patient ID does not exist.");return;}double v=DataUtil.toDouble(amt,-1);if(v<0){JOptionPane.showMessageDialog(this,"Enter a valid amount.");return;}String id=FileHandler.nextId(FileHandler.BILLING,"B",4);FileHandler.append(FileHandler.BILLING,new String[]{id,p,a,String.format("%.2f",v),"UNPAID",DataUtil.today()});patientField.setText("");assessmentField.setText("");amountField.setText("");refresh();}
    private void markPaid(){int i=table.getSelectedRow();if(i<0){JOptionPane.showMessageDialog(this,"Select a bill.");return;}String id=model.getValueAt(i,0).toString();List<String[]>rows=new ArrayList<>(FileHandler.read(FileHandler.BILLING));for(String[]r:rows)if(r.length>=5&&r[0].equals(id))r[4]="PAID";FileHandler.writeAll(FileHandler.BILLING,rows);refresh();}
    private void del(){int i=table.getSelectedRow();if(i<0)return;String id=model.getValueAt(i,0).toString();List<String[]>rows=new ArrayList<>(FileHandler.read(FileHandler.BILLING));rows.removeIf(r->r.length>0&&r[0].equals(id));FileHandler.writeAll(FileHandler.BILLING,rows);refresh();}
}
