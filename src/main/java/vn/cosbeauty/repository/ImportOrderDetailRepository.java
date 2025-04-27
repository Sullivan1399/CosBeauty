package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.ImportOrder;
import vn.cosbeauty.entity.ImportOrderDetail;

import java.util.List;

public interface ImportOrderDetailRepository extends JpaRepository<ImportOrderDetail, Long> {
    List<ImportOrderDetail> findByImportOrder(ImportOrder importOrder);
}
