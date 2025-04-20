package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.ImportOrder;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, Long> {
}
