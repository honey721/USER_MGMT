package com.booksStore.booksStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.booksStore.booksStore.entity.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
