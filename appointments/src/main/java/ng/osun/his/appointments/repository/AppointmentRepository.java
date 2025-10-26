package ng.osun.his.appointments.repository;

import ng.osun.his.appointments.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(String patientId);
    List<Appointment> findByClinicIdAndAppointmentDateBetween(String clinicId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByProviderIdAndAppointmentDateBetween(String providerId, LocalDateTime start, LocalDateTime end);
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    List<Appointment> findByStatus(String status);
    long countByClinicIdAndAppointmentDateBetween(String clinicId, LocalDateTime start, LocalDateTime end);
}

