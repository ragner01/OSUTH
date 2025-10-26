package ng.osun.his.billing.repository;

import ng.osun.his.billing.domain.PriceBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceBookRepository extends JpaRepository<PriceBook, String> {
    Optional<PriceBook> findByServiceCode(String serviceCode);
    List<PriceBook> findByDepartmentAndActiveTrue(String department);
    List<PriceBook> findByActiveTrue();
}

