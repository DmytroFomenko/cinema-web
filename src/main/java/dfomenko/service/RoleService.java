package dfomenko.service;


import dfomenko.entity.Role;

public interface RoleService {
    Role getRoleById(Long id);
    Role findRoleByRole(String role);
}
