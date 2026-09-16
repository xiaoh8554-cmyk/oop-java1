package patients.ui;

import common.*;
import patients.ux.MedicalRecordUX;

public class MedicalRecordUI extends MedicalRecordUX {
    public MedicalRecordUI(){super(); backButton.addActionListener(e -> dispose());refresh();refreshButton.addActionListener(e->refresh());}
    private void refresh(){model.setRowCount(0);String patient=Session.getCurrentUser().getUserId();for(String[]r:FileHandler.read(FileHandler.ASSESSMENTS))if(r.length>=10&&r[1].equals(patient))model.addRow(r);}
}
