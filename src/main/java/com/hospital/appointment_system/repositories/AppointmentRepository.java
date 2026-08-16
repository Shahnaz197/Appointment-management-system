package com.hospital.appointment_system.repositories;

import com.hospital.appointment_system.models.Appointment;
import com.hospital.appointment_system.models.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    boolean existsByDoctorIdAndAppointmentDateAndTimeSlot(
            Long doctorId, LocalDate date, LocalTime timeSlot
    );
    List<Appointment> findByStatus(AppointmentStatus status);
}