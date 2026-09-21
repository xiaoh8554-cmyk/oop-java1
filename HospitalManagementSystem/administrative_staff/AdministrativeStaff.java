package administrative_staff;

import common.model.User;
import common.model.UserRole;

public class AdministrativeStaff extends User {

    public AdministrativeStaff(String id, String email, String password, String fullName, String phoneNumber) {
        super(id, email, password, fullName, phoneNumber, UserRole.ADMINISTRATIVE_STAFF);
    }

    public AdministrativeStaff(String id, String email, String password, String fullName, String phoneNumber,
                               String accessLevel) {
        this(id, email, password, fullName, phoneNumber);
    }

    // ID
    @Override
    public String toChildFileString() {
        return sanitize(getId());
    }
}
