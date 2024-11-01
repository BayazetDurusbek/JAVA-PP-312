package ru.example.springsecurity.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.springsecurity.models.Role;

import java.util.Set;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);

    Role getRoleByName(String name);

    Set<Role> getRolesByName(String name);
}
