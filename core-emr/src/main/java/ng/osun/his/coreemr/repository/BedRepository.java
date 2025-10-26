package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, String> {
    List<Bed> findByWardId(String wardId);
    List<Bed> findByStatus(String status);
    Optional<Bed> findByCode(String code);
}

