import java.util.Date;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private Date registrationDate;

    public User(Long id, String username, String email, String password, String fullName, Date registrationDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
    }

    // Getters and setters
}
