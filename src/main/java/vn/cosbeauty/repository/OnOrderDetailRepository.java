package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.OnOrderDetail;
import vn.cosbeauty.entity.OnlineOrder;
import vn.cosbeauty.entity.Product;

import java.util.List;

public interface OnOrderDetailRepository extends JpaRepository<OnOrderDetail, Long> {
    // Có thể khai báo thêm các phương thức truy vấn nếu cần
    List<OnOrderDetail> findOnOrderDetailByOnlineOrder_OnOrderID(Long orderId);
//    long countByCustomerAndProduct(Customer customer,Product product);
}
