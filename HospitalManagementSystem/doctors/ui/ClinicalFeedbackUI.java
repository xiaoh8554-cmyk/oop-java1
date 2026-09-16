package doctors.ui;

import common.*;
import doctors.ux.ClinicalFeedbackUX;
import javax.swing.*;

public class ClinicalFeedbackUI extends ClinicalFeedbackUX {
    public ClinicalFeedbackUI(){super(); backButton.addActionListener(e -> dispose());refresh();saveButton.addActionListener(e->save());refreshButton.addActionListener(e->refresh());}
    private void refresh(){model.setRowCount(0);String doctor=Session.getCurrentUser().getUserId();for(String[]r:FileHandler.read(FileHandler.FEEDBACK))if(r.length>=6&&r[2].equals(doctor))model.addRow(r);}
    private void save(){String patient=patientField.getText().trim(),msg=messageArea.getText().trim();if(!AuthService.userIdExists(patient,"PATIENT")){JOptionPane.showMessageDialog(this,"Patient ID does not exist.");return;}if(msg.isEmpty()){JOptionPane.showMessageDialog(this,"Feedback message is required.");return;}String id=FileHandler.nextId(FileHandler.FEEDBACK,"F",4);FileHandler.append(FileHandler.FEEDBACK,new String[]{id,patient,Session.getCurrentUser().getUserId(),DataUtil.today(),"CLINICAL_FEEDBACK",msg});patientField.setText("");messageArea.setText("");refresh();}
}
