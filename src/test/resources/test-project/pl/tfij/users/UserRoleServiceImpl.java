import java.util.HashSet;
import java.util.Set;

public class UserRoleServiceImpl implements UserRoleService {
    private final Map<Long, Set<Role>> userRoles = new HashMap<>();

    @Override
    public void assignRole(Long userId, Role role) {
        userRoles.computeIfAbsent(userId, k -> new HashSet<>()).add(role);
    }

    // Other methods from UserRoleService interface
}
