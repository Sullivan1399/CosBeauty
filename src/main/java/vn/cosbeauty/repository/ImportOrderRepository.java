package vn.cosbeauty.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Import đúng Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.ImportOrder;

import java.time.LocalDate;
import java.util.List;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, Long> {
    Page<ImportOrder> findByImportDateAndStatus(LocalDate importDate, Integer status, Pageable pageable);

    Page<ImportOrder> findByImportDate(LocalDate importDate, Pageable pageable);

    Page<ImportOrder> findByStatus(Integer status, Pageable pageable);
}

