package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.cosbeauty.entity.OffOrderDetail;

import java.util.List;

public interface OffOrderDetailRepository extends JpaRepository<OffOrderDetail, Long>{
	@Query("SELECT o FROM OffOrderDetail o WHERE o.offlineOrder.offOrderID = :id")
	List<OffOrderDetail> getOffOrderDetailByOffOrderId(@Param("id") Long id);
}
