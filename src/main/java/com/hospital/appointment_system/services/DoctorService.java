package com.hospital.appointment_system.services;

import com.hospital.appointment_system.exceptions.DoctorNotFoundException;
import com.hospital.appointment_system.models.Doctor;
import com.hospital.appointment_system.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }

    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Doctor existing = getDoctorById(id);
        existing.setName(updatedDoctor.getName());
        existing.setPhone(updatedDoctor.getPhone());
        existing.setSpecialization(updatedDoctor.getSpecialization());
        existing.setAvailableFrom(updatedDoctor.getAvailableFrom());
        existing.setAvailableTo(updatedDoctor.getAvailableTo());
        existing.setAvailable(updatedDoctor.isAvailable());
        return doctorRepository.save(existing);
    }

    public void deleteDoctor(Long id) {
        Doctor existing = getDoctorById(id);
        doctorRepository.delete(existing);
    }
}
