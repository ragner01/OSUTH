package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByActiveTrue();
    Optional<Ward> findByCode(String code);
}

