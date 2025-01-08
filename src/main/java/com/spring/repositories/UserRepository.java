package com.spring.repositories;

import java.util.Optional;
import java.util.UUID;

import com.spring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

}
