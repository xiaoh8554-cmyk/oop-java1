package administrative_staff;

import common.model.User;
import common.model.UserRole;

public class AdministrativeStaff extends User {
    private String accessLevel;

    public AdministrativeStaff(String id, String email, String password, String fullName, String phoneNumber,
                               String accessLevel) {
        super(id, email, password, fullName, phoneNumber, UserRole.ADMINISTRATIVE_STAFF);
        this.accessLevel = accessLevel;
    }

    // ID|accessLevel
    @Override
    public String toChildFileString() {
        return String.join("|", sanitize(getId()), sanitize(accessLevel));
    }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
}
