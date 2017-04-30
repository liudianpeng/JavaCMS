package testBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import testBlog.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
