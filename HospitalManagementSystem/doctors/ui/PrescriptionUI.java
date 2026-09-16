package doctors.ui;

import common.*;
import doctors.ux.PrescriptionUX;
import javax.swing.*;

public class PrescriptionUI extends PrescriptionUX {
    public PrescriptionUI(){super(); backButton.addActionListener(e -> dispose());refresh();saveButton.addActionListener(e->save());refreshButton.addActionListener(e->refresh());}
    private void refresh(){model.setRowCount(0);String doctor=Session.getCurrentUser().getUserId();for(String[]r:FileHandler.read(FileHandler.PRESCRIPTIONS))if(r.length>=6&&r[2].equals(doctor))model.addRow(r);}
    private void save(){String patient=patientField.getText().trim(),med=medicineField.getText().trim(),ins=instructionsArea.getText().trim();if(!AuthService.userIdExists(patient,"PATIENT")){JOptionPane.showMessageDialog(this,"Patient ID does not exist.");return;}if(med.isEmpty()||ins.isEmpty()){JOptionPane.showMessageDialog(this,"Medicine and instructions are required.");return;}String id=FileHandler.nextId(FileHandler.PRESCRIPTIONS,"RX",4);FileHandler.append(FileHandler.PRESCRIPTIONS,new String[]{id,patient,Session.getCurrentUser().getUserId(),DataUtil.today(),med,ins});patientField.setText("");medicineField.setText("");instructionsArea.setText("");refresh();JOptionPane.showMessageDialog(this,"Prescription issued successfully.");}
}
