package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
