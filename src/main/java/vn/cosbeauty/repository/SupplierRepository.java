package vn.cosbeauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.cosbeauty.entity.Supplier;

public interface  SupplierRepository extends JpaRepository<Supplier, Integer> {
	@Query("SELECT s FROM Supplier s WHERE LOWER(s.supName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
	List<Supplier> findSuppierBySupName(@Param("keyword") String keyword);
    
}
