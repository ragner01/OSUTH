package ng.osun.his.pharmacy.repository;

import ng.osun.his.pharmacy.domain.FormularyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormularyItemRepository extends JpaRepository<FormularyItem, String> {
    Optional<FormularyItem> findByMedication_Id(String medicationId);
    List<FormularyItem> findByActiveTrue();
    List<FormularyItem> findByRequiresPrescriptionTrue();
}

