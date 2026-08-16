package com.hospital.appointment_system.controllers;

import com.hospital.appointment_system.models.Appointment;
import com.hospital.appointment_system.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime timeSlot) {
        try {
            Appointment appointment = appointmentService.bookAppointment(
                    patientId, doctorId, date, timeSlot);
            return new ResponseEntity<>(appointment, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsByDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<Appointment>> getByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable LocalDate date) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsByDoctorAndDate(
                        doctorId, date));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                appointmentService.cancelAppointment(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Appointment> completeAppointment(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                appointmentService.completeAppointment(id));
    }

    @GetMapping("/waitlist/size")
    public ResponseEntity<String> getWaitlistSize() {
        return ResponseEntity.ok(
                "Waitlist size: " + appointmentService.getWaitlistSize());
    }
}

