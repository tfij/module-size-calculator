public class UserRegistrationValidator {
    public boolean validateUser(User user) {
        // Placeholder validation logic
        return user.getUsername() != null && !user.getUsername().isEmpty() &&
                user.getEmail() != null && !user.getEmail().isEmpty() &&
                user.getPassword() != null && !user.getPassword().isEmpty();
    }
}