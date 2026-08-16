package com.hospital.appointment_system.services;


import com.hospital.appointment_system.exceptions.AppointmentConflictException;
import com.hospital.appointment_system.exceptions.DoctorNotFoundException;
import com.hospital.appointment_system.exceptions.PatientNotFoundException;
import com.hospital.appointment_system.models.Appointment;
import com.hospital.appointment_system.models.AppointmentStatus;
import com.hospital.appointment_system.models.Doctor;
import com.hospital.appointment_system.models.Patient;
import com.hospital.appointment_system.repositories.AppointmentRepository;
import com.hospital.appointment_system.repositories.DoctorRepository;
import com.hospital.appointment_system.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // waitlist queue — doctor id maps to queue of patient ids
    private Queue<Long[]> waitlistQueue = new LinkedList<>();

    // synchronized — only one booking at a time!
    public synchronized Appointment bookAppointment(
            Long patientId,
            Long doctorId,
            LocalDate date,
            LocalTime timeSlot) {

        // check patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient not found: " + patientId));

        // check doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor not found: " + doctorId));

        // check if slot already taken
        boolean slotTaken = appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndTimeSlot(
                        doctorId, date, timeSlot);

        if (slotTaken) {
            // add to waitlist
            waitlistQueue.offer(new Long[]{patientId, doctorId});
            throw new AppointmentConflictException(
                    "Slot already taken! Added to waitlist. Position: "
                            + waitlistQueue.size());
        }

        // create appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(date);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(
            Long doctorId, LocalDate date) {
        return appointmentRepository
                .findByDoctorIdAndAppointmentDate(doctorId, date);
    }

    public Appointment cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found: " + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);

        // check waitlist — if someone waiting, notify them
        if (!waitlistQueue.isEmpty()) {
            Long[] next = waitlistQueue.poll();
            System.out.println("Notifying patient " + next[0]
                    + " that slot is now available with doctor " + next[1]);
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found: " + id));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    public int getWaitlistSize() {
        return waitlistQueue.size();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}

