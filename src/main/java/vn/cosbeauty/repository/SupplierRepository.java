package vn.cosbeauty.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.cosbeauty.entity.Supplier;

public interface  SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findBySupNameIgnoreCase(String supName);
}
