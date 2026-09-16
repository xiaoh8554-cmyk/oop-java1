package doctors.ui;

import common.*;
import doctors.ux.AssessmentResultUX;
import javax.swing.*;
import java.util.*;

public class AssessmentResultUI extends AssessmentResultUX {
    private final Map<String,String[]> typeMap=new LinkedHashMap<>();
    public AssessmentResultUI(){super(); backButton.addActionListener(e -> dispose());loadTypes();refresh();saveButton.addActionListener(e->save());refreshButton.addActionListener(e->{loadTypes();refresh();});}
    private void loadTypes(){typeBox.removeAllItems();typeMap.clear();for(String[]r:FileHandler.read(FileHandler.ASSESSMENT_TYPES))if(r.length>=4){String display=r[0]+" - "+r[1];typeMap.put(display,r);typeBox.addItem(display);}}
    private void refresh(){model.setRowCount(0);String doctorId=Session.getCurrentUser().getUserId();for(String[]r:FileHandler.read(FileHandler.ASSESSMENTS))if(r.length>=10&&r[2].equals(doctorId))model.addRow(r);}
    private void save(){String patient=patientField.getText().trim();String result=resultArea.getText().trim(),lab=labArea.getText().trim();if(!AuthService.userIdExists(patient,"PATIENT")){JOptionPane.showMessageDialog(this,"Patient ID does not exist.");return;}String selected=(String)typeBox.getSelectedItem();if(selected==null){JOptionPane.showMessageDialog(this,"Create an assessment type first.");return;}if(result.isEmpty()){JOptionPane.showMessageDialog(this,"Assessment result is required.");return;}String[]type=typeMap.get(selected);String assessmentId=FileHandler.nextId(FileHandler.ASSESSMENTS,"AS",4);String doctor=Session.getCurrentUser().getUserId();String fee=type[3];FileHandler.append(FileHandler.ASSESSMENTS,new String[]{assessmentId,patient,doctor,type[0],DataUtil.today(),"PENDING_REVIEW",result,lab,fee,"COMPLETED"});String billId=FileHandler.nextId(FileHandler.BILLING,"B",4);FileHandler.append(FileHandler.BILLING,new String[]{billId,patient,assessmentId,fee,"UNPAID",DataUtil.today()});patientField.setText("");resultArea.setText("");labArea.setText("");refresh();JOptionPane.showMessageDialog(this,"Assessment saved and bill "+billId+" generated automatically.");}
}
