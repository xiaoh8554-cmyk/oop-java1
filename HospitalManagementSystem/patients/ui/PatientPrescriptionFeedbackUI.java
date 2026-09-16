package patients.ui;

import common.*;
import patients.ux.PatientPrescriptionFeedbackUX;
import javax.swing.*;

public class PatientPrescriptionFeedbackUI extends PatientPrescriptionFeedbackUX {
    public PatientPrescriptionFeedbackUI(){super(); backButton.addActionListener(e -> dispose());refresh();refreshButton.addActionListener(e->refresh());submitButton.addActionListener(e->submit());}
    private void refresh(){String patient=Session.getCurrentUser().getUserId();prescriptionModel.setRowCount(0);for(String[]r:FileHandler.read(FileHandler.PRESCRIPTIONS))if(r.length>=6&&r[1].equals(patient))prescriptionModel.addRow(r);feedbackModel.setRowCount(0);for(String[]r:FileHandler.read(FileHandler.FEEDBACK))if(r.length>=6&&r[1].equals(patient))feedbackModel.addRow(r);}
    private void submit(){String doctor=doctorField.getText().trim(),msg=feedbackArea.getText().trim();if(!AuthService.userIdExists(doctor,"DOCTOR")){JOptionPane.showMessageDialog(this,"Doctor ID does not exist.");return;}if(msg.isEmpty()){JOptionPane.showMessageDialog(this,"Please enter feedback.");return;}String id=FileHandler.nextId(FileHandler.FEEDBACK,"F",4);FileHandler.append(FileHandler.FEEDBACK,new String[]{id,Session.getCurrentUser().getUserId(),doctor,DataUtil.today(),"PATIENT_FEEDBACK",msg});doctorField.setText("");feedbackArea.setText("");refresh();JOptionPane.showMessageDialog(this,"Thank you. Your feedback has been recorded.");}
}
