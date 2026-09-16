package medical_manager;

import common.model.User;
import common.model.UserRole;

public class MedicalManager extends User {
    private String department;
    private String officeNumber;

    public MedicalManager(String id, String email, String password, String fullName, String phoneNumber,
                          String department, String officeNumber) {
        super(id, email, password, fullName, phoneNumber, UserRole.MEDICAL_MANAGER);
        this.department = department;
        this.officeNumber = officeNumber;
    }

    // ID|department|officeNumber
    @Override
    public String toChildFileString() {
        return String.join("|",
                sanitize(getId()),
                sanitize(department),
                sanitize(officeNumber)
        );
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
}
