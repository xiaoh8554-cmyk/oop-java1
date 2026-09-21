package medical_manager;

import common.model.User;
import common.model.UserRole;

public class MedicalManager extends User {
    private String officeNumber;

    public MedicalManager(String id, String email, String password, String fullName, String phoneNumber,
                          String officeNumber) {
        super(id, email, password, fullName, phoneNumber, UserRole.MEDICAL_MANAGER);
        this.officeNumber = officeNumber;
    }

    public MedicalManager(String id, String email, String password, String fullName, String phoneNumber,
                          String department, String officeNumber) {
        this(id, email, password, fullName, phoneNumber, officeNumber);
    }

    // ID|officeNumber
    @Override
    public String toChildFileString() {
        return String.join("|",
                sanitize(getId()),
                sanitize(officeNumber)
        );
    }

    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
}
