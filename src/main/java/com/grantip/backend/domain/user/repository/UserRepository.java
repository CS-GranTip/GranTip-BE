package com.grantip.backend.domain.user.repository;

import com.grantip.backend.domain.user.entity.Role;
import com.grantip.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String LoginId);

    boolean existsByLoginId(String LoginId);
    List<User> findByRole(Role role);
}
