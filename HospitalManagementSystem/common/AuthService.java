package common;

import common.model.User;
import common.model.UserRole;
import java.util.List;

public class AuthService {
    private static volatile AuthService instance;
    private final DataManager dataManager;
    private User currentUser;

    public enum AuthStatus {
        SUCCESS("Login successful."),
        USER_NOT_FOUND("No account found with this email address."),
        INVALID_PASSWORD("Incorrect password entered."),
        EMPTY_FIELDS("Please enter both email and password.");

        private final String message;
        AuthStatus(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    private AuthService() {
        this.dataManager = DataManager.getInstance();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            synchronized (AuthService.class) {
                if (instance == null) {
                    instance = new AuthService();
                }
            }
        }
        return instance;
    }

    // Static helper to match existing codebase calls
    public static User login(String identifier, String password) {
        AuthService auth = getInstance();
        AuthStatus status = auth.authenticate(identifier, password);
        return (status == AuthStatus.SUCCESS) ? auth.getCurrentUser() : null;
    }

    public synchronized AuthStatus authenticate(String identifier, String password) {
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return AuthStatus.EMPTY_FIELDS;
        }

        String cleanIdentifier = identifier.trim();

        // 1. Check if user is logging in as 'admin' alias
        if (cleanIdentifier.equalsIgnoreCase("admin")) {
            for (User u : dataManager.getAllUsers()) {
                if (u.getRole() == UserRole.ADMINISTRATIVE_STAFF && u.getPassword().equals(password)) {
                    this.currentUser = u;
                    Session.setCurrentUser(u);
                    return AuthStatus.SUCCESS;
                }
            }
        }

        // 2. Lookup strictly by Email address
        User user = dataManager.findByEmail(cleanIdentifier);
        if (user == null) {
            return AuthStatus.USER_NOT_FOUND;
        }

        if (!user.getPassword().equals(password)) {
            return AuthStatus.INVALID_PASSWORD;
        }

        this.currentUser = user;
        Session.setCurrentUser(user);
        return AuthStatus.SUCCESS;
    }

    public synchronized void logout() {
        this.currentUser = null;
        Session.clear();
    }

    public User getCurrentUser() {
        return currentUser != null ? currentUser : Session.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public static boolean usernameExists(String email) {
        return getInstance().dataManager.isEmailTaken(email);
    }

    public static boolean emailExists(String email) {
        return getInstance().dataManager.isEmailTaken(email);
    }

    public static boolean userIdExists(String userId, String requiredRole) {
        if (userId == null) return false;
        User user = getInstance().dataManager.findById(userId.trim());
        if (user == null) return false;
        if (requiredRole == null) return true;
        return user.getRole().name().equalsIgnoreCase(requiredRole) ||
               (requiredRole.equals("ADMIN") && user.getRole() == UserRole.ADMINISTRATIVE_STAFF) ||
               (requiredRole.equals("MANAGER") && user.getRole() == UserRole.MEDICAL_MANAGER);
    }
}
