package com.hospital.appointment_system.services;

import com.hospital.appointment_system.exceptions.DoctorNotFoundException;
import com.hospital.appointment_system.models.Doctor;
import com.hospital.appointment_system.repositories.DoctorRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Cacheable(value = "doctors", key = "#id")
    public Doctor getDoctorById(Long id) {
        System.out.println("fetching from DATABASE for id: " + id);
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor not found with id: " + id));
    }

    @Cacheable(value = "allDoctors")
    public List<Doctor> getAllDoctors() {
        System.out.println("fetching ALL doctors from DATABASE");
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }

    @CachePut(value = "doctors", key = "#id")
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

    @CacheEvict(value = {"doctors", "allDoctors"}, allEntries = true)
    public void deleteDoctor(Long id) {
        Doctor existing = getDoctorById(id);
        doctorRepository.delete(existing);
    }

    public Page<Doctor> getAllDoctorsPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return doctorRepository.findAll(pageable);
    }

    // paginated by specialization
    public Page<Doctor> getDoctorsBySpecializationPaginated(
            String specialization, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return doctorRepository.findBySpecialization(specialization, pageable);
    }
}
