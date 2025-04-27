package vn.cosbeauty.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.cosbeauty.entity.OfflineOrder;


public interface OfflineOrderRepository extends JpaRepository<OfflineOrder, Long>{
	Page<OfflineOrder> findByOrderDate(LocalDate orderDate, Pageable pageable);
	
	Page<OfflineOrder> findByPhone(String phone, Pageable pageable);
	
	List<OfflineOrder> findAllBy();
	@Query("SELECT o FROM OfflineOrder o WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "o.phone LIKE CONCAT('%', :keyword, '%'))")
	
	List<OfflineOrder> findByNameContainingIgnoreCaseOrPhoneContaining(@Param("keyword") String keyword);
}
