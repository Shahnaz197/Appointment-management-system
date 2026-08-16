package com.hospital.appointment_system.repositories;

import com.hospital.appointment_system.models.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByAvailableTrue();

    //pagination
    Page<Doctor> findAll(Pageable pageable); // ← already inherited from JpaRepository!
    Page<Doctor> findBySpecialization(String specialization, Pageable pageable);
    Page<Doctor> findByAvailableTrue(Pageable pageable);
}


