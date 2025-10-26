package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByMrn(String mrn);
    Optional<Patient> findByNin(String nin);
    Optional<Patient> findByPrimaryPhone(String primaryPhone);
    
    @Query("SELECT p FROM Patient p WHERE p.deleted = false")
    Page<Patient> findAllActive(Pageable pageable);
    
    Page<Patient> findByMrnContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNinContainingIgnoreCaseOrPrimaryPhoneContainingIgnoreCase(
        String mrn, String firstName, String lastName, String nin, String phone, Pageable pageable);
    
    @Query("SELECT p FROM Patient p WHERE p.deleted = false AND (LOWER(p.mrn) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR p.nin LIKE CONCAT('%', :query, '%') OR p.primaryPhone LIKE CONCAT('%', :query, '%'))")
    Page<Patient> search(@Param("query") String query, Pageable pageable);
    
    List<Patient> findByDeletedFalseAndActiveTrue();
}

