package ng.osun.his.appointments.repository;

import ng.osun.his.appointments.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    List<Provider> findByActiveTrue();
    Optional<Provider> findByUserId(String userId);
    List<Provider> findByDepartmentAndActiveTrue(String department);
}

