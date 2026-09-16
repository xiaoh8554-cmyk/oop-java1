package medical_manager.ui;

import common.FileHandler;
import medical_manager.ux.MedicalGradingUX;
import javax.swing.*;
import java.util.*;

public class MedicalGradingUI extends MedicalGradingUX {
    public MedicalGradingUI(){super(); backButton.addActionListener(e -> dispose());refresh();refreshButton.addActionListener(e->refresh());updateButton.addActionListener(e->updateGrade());}
    private void refresh(){model.setRowCount(0);for(String[]r:FileHandler.read(FileHandler.ASSESSMENTS))if(r.length>=10)model.addRow(r);}
    private void updateGrade(){int i=table.getSelectedRow();if(i<0){JOptionPane.showMessageDialog(this,"Select an assessment.");return;}String id=model.getValueAt(i,0).toString();List<String[]>rows=new ArrayList<>(FileHandler.read(FileHandler.ASSESSMENTS));for(String[]r:rows)if(r.length>=10&&r[0].equals(id)){r[5]=gradeBox.getSelectedItem().toString();r[9]=statusBox.getSelectedItem().toString();}FileHandler.writeAll(FileHandler.ASSESSMENTS,rows);refresh();JOptionPane.showMessageDialog(this,"Medical grade updated.");}
}
