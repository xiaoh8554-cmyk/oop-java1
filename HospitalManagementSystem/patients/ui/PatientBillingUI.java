package patients.ui;

import common.*;
import patients.ux.PatientBillingUX;
import javax.swing.*;
import java.util.*;

public class PatientBillingUI extends PatientBillingUX {
    public PatientBillingUI(){super(); backButton.addActionListener(e -> dispose());refresh();refreshButton.addActionListener(e->refresh());payButton.addActionListener(e->pay());}
    private void refresh(){model.setRowCount(0);String patient=Session.getCurrentUser().getUserId();double outstanding=0;for(String[]r:FileHandler.read(FileHandler.BILLING))if(r.length>=6&&r[1].equals(patient)){model.addRow(r);if(!r[4].equals("PAID"))outstanding+=DataUtil.toDouble(r[3],0);}totalLabel.setText(String.format("Outstanding: RM %.2f",outstanding));}
    private void pay(){int i=table.getSelectedRow();if(i<0){JOptionPane.showMessageDialog(this,"Select an unpaid bill.");return;}String id=model.getValueAt(i,0).toString();String status=model.getValueAt(i,4).toString();if(status.equals("PAID")){JOptionPane.showMessageDialog(this,"This bill is already paid.");return;}int answer=JOptionPane.showConfirmDialog(this,"Confirm payment for bill "+id+"?","Payment",JOptionPane.YES_NO_OPTION);if(answer!=JOptionPane.YES_OPTION)return;List<String[]>rows=new ArrayList<>(FileHandler.read(FileHandler.BILLING));for(String[]r:rows)if(r.length>=5&&r[0].equals(id))r[4]="PAID";FileHandler.writeAll(FileHandler.BILLING,rows);refresh();JOptionPane.showMessageDialog(this,"Payment recorded successfully.");}
}
