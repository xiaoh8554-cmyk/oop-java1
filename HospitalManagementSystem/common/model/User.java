package common.model;

import java.util.Objects;

public abstract class User {
    public static final String[] COUNTRY_CODES = new String[]{
        "+60", "+65", "+62", "+66", "+63", "+84", "+86", "+852", "+886", "+81", "+82", "+91", "+1", "+44", "+61", "+64", "+971"
    };

    private String id;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private UserRole role;

    public User(String id, String email, String password, String fullName, String phoneNumber, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // ROLE|ID|EMAIL|PASSWORD|FULL_NAME|PHONE
    public String toBaseFileString() {
        return String.join("|",
                role.name(),
                sanitize(id),
                sanitize(email),
                sanitize(password),
                sanitize(fullName),
                sanitize(phoneNumber)
        );
    }

    public abstract String toChildFileString();

    protected static String sanitize(String input) {
        if (input == null) return "";
        return input.replace("|", "/").replace("\n", " ").replace("\r", " ").trim();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return id; } // Backward compatibility
    public String getUsername() { return email; } // Backward compatibility (returns email)

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getRoleName() { return role != null ? role.name() : ""; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
