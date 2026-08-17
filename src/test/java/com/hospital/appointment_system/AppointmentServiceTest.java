package com.hospital.appointment_system;

import com.hospital.appointment_system.exceptions.AppointmentConflictException;
import com.hospital.appointment_system.exceptions.DoctorNotFoundException;
import com.hospital.appointment_system.exceptions.PatientNotFoundException;
import com.hospital.appointment_system.models.*;
import com.hospital.appointment_system.repositories.AppointmentRepository;
import com.hospital.appointment_system.repositories.DoctorRepository;
import com.hospital.appointment_system.repositories.PatientRepository;
import com.hospital.appointment_system.services.AppointmentService;
import com.hospital.appointment_system.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private LocalDate date;
    private LocalTime timeSlot;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Silva");
        patient.setEmail("john@gmail.com");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Sarah Smith");
        doctor.setSpecialization("Cardiology");

        date = LocalDate.of(2026, 8, 25);
        timeSlot = LocalTime.of(9, 0);
    }

    // TEST 1 — book appointment successfully
    @Test
    void bookAppointment_shouldCreateAppointment_whenSlotAvailable() {
        // arrange
        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));
        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndTimeSlot(1L, date, timeSlot))
                .thenReturn(false);

        Appointment savedAppointment = new Appointment();
        savedAppointment.setId(1L);
        savedAppointment.setPatient(patient);
        savedAppointment.setDoctor(doctor);
        savedAppointment.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(savedAppointment);

        // act
        Appointment result = appointmentService.bookAppointment(
                1L, 1L, date, timeSlot);

        // assert
        assertNotNull(result);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());
        assertEquals("John Silva", result.getPatient().getName());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    // TEST 2 — book appointment slot taken
    @Test
    void bookAppointment_shouldThrowException_whenSlotTaken() {
        // arrange
        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));
        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndTimeSlot(1L, date, timeSlot))
                .thenReturn(true); // slot is taken!

        // act & assert
        assertThrows(AppointmentConflictException.class, () -> {
            appointmentService.bookAppointment(1L, 1L, date, timeSlot);
        });
        verify(appointmentRepository, never()).save(any());
    }

    // TEST 3 — book appointment patient not found
    @Test
    void bookAppointment_shouldThrowException_whenPatientNotFound() {
        // arrange
        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(PatientNotFoundException.class, () -> {
            appointmentService.bookAppointment(99L, 1L, date, timeSlot);
        });
    }

    // TEST 4 — book appointment doctor not found
    @Test
    void bookAppointment_shouldThrowException_whenDoctorNotFound() {
        // arrange
        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));
        when(doctorRepository.findById(99L))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(DoctorNotFoundException.class, () -> {
            appointmentService.bookAppointment(1L, 99L, date, timeSlot);
        });
    }

    // TEST 5 — cancel appointment
    @Test
    void cancelAppointment_shouldUpdateStatus_toCancelled() {
        // arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        // act
        Appointment result = appointmentService.cancelAppointment(1L);

        // assert
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    // TEST 6 — complete appointment
    @Test
    void completeAppointment_shouldUpdateStatus_toCompleted() {
        // arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        // act
        Appointment result = appointmentService.completeAppointment(1L);

        // assert
        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }
}