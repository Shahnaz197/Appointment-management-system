package com.hospital.appointment_system.services;

import com.hospital.appointment_system.models.Appointment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {
        try {
            // simulating email sending delay
            Thread.sleep(3000);
            System.out.println("📧 Confirmation email sent to: "
                    + appointment.getPatient().getEmail()
                    + " for appointment on "
                    + appointment.getAppointmentDate()
                    + " at "
                    + appointment.getTimeSlot());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("notification interrupted!");
        }
    }

    @Async
    public void sendCancellationNotification(Appointment appointment) {
        try {
            Thread.sleep(2000);
            System.out.println("📧 Cancellation email sent to: "
                    + appointment.getPatient().getEmail()
                    + " appointment on "
                    + appointment.getAppointmentDate()
                    + " has been cancelled!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("notification interrupted!");
        }
    }

    @Async
    public void sendWaitlistNotification(Long patientId, Long doctorId) {
        try {
            Thread.sleep(1000);
            System.out.println("📧 Waitlist notification sent to patient: "
                    + patientId
                    + " slot available with doctor: "
                    + doctorId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("notification interrupted!");
        }
    }
}
