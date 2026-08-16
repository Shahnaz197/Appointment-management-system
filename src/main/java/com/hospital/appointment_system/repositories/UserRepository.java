package com.hospital.appointment_system.repositories;

import com.hospital.appointment_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository <User,Long >{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
