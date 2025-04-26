package vn.cosbeauty.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.cosbeauty.entity.Supplier;

public interface  SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findBySupNameIgnoreCase(String supName);
    Supplier findById(int id);
    Page<Supplier> findBySupNameContainingIgnoreCase(String keyword, Pageable pageable);
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:keyword IS NULL OR s.supName LIKE %:keyword% OR s.email LIKE %:keyword%)")
    Page<Supplier> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
