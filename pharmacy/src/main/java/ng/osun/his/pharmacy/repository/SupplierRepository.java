package ng.osun.his.pharmacy.repository;

import ng.osun.his.pharmacy.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, String> {
    Optional<Supplier> findByCode(String code);
    List<Supplier> findByActiveTrue();
}

