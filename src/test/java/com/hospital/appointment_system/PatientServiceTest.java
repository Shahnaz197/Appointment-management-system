package com.hospital.appointment_system;

import com.hospital.appointment_system.exceptions.PatientNotFoundException;
import com.hospital.appointment_system.models.Patient;
import com.hospital.appointment_system.repositories.PatientRepository;
import com.hospital.appointment_system.services.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Silva");
        patient.setEmail("john@gmail.com");
        patient.setPhone("0771234567");
    }

    // TEST 1 — get patient by id successfully
    @Test
    void getPatientById_shouldReturnPatient_whenPatientExists() {
        // arrange
        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        // act
        Patient result = patientService.getPatientById(1L);

        // assert
        assertNotNull(result);
        assertEquals("John Silva", result.getName());
        assertEquals("john@gmail.com", result.getEmail());
        verify(patientRepository, times(1)).findById(1L);
    }

    // TEST 2 — get patient by id not found
    @Test
    void getPatientById_shouldThrowException_whenPatientNotFound() {
        // arrange
        when(patientRepository.findById(99L))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(PatientNotFoundException.class, () -> {
            patientService.getPatientById(99L);
        });
        verify(patientRepository, times(1)).findById(99L);
    }

    // TEST 3 — create patient successfully
    @Test
    void createPatient_shouldSaveAndReturnPatient() {
        // arrange
        when(patientRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);
        when(patientRepository.save(patient))
                .thenReturn(patient);

        // act
        Patient result = patientService.createPatient(patient);

        // assert
        assertNotNull(result);
        assertEquals("John Silva", result.getName());
        verify(patientRepository, times(1)).save(patient);
    }

    // TEST 4 — create patient with duplicate email
    @Test
    void createPatient_shouldThrowException_whenEmailExists() {
        // arrange
        when(patientRepository.existsByEmail("john@gmail.com"))
                .thenReturn(true);

        // act & assert
        assertThrows(IllegalArgumentException.class, () -> {
            patientService.createPatient(patient);
        });
        verify(patientRepository, never()).save(any());
    }

    // TEST 5 — get all patients
    @Test
    void getAllPatients_shouldReturnAllPatients() {
        // arrange
        Patient patient2 = new Patient();
        patient2.setName("Sara");
        patient2.setEmail("sara@gmail.com");

        when(patientRepository.findAll())
                .thenReturn(Arrays.asList(patient, patient2));

        // act
        List<Patient> result = patientService.getAllPatients();

        // assert
        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findAll();
    }

    // TEST 6 — delete patient
    @Test
    void deletePatient_shouldDeleteSuccessfully_whenPatientExists() {
        // arrange
        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        // act
        patientService.deletePatient(1L);

        // assert
        verify(patientRepository, times(1)).delete(patient);
    }
}