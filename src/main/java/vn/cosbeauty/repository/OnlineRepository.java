package vn.cosbeauty.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.OnOrderDetail;
import vn.cosbeauty.entity.OnlineOrder;
import vn.cosbeauty.entity.Product;

import java.util.List;

@Repository
public interface OnlineRepository extends JpaRepository<OnlineOrder, Long> {
    List<OnlineOrder> findOnlineOrderByCustomerOrderByOrderDateDesc(Customer customer);
    List<OnlineOrder> findOnlineOrderByCustomerAndConfirm(Customer customer, Integer confirm);
    List<OnlineOrder> findOnlineOrderByCustomerAndDeliveryStatus(Customer customer, Integer delivery);
    List<OnlineOrder> findByOnOrderIDOrderByDeliveryDateDesc(Long orderId);
    long countByCustomerAndOnOrderDetails_Product(Customer customer, Product product);
    @Query("SELECT COUNT(od) FROM OnOrderDetail od WHERE od.onlineOrder = :order AND od.product = :product")
    long countByOrderAndProduct(@Param("order") OnlineOrder order, @Param("product") Product product);


    List<OnlineOrder> getAllBy();
    @Query("SELECT o FROM OnlineOrder o WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "o.phone LIKE CONCAT('%', :keyword, '%'))")
    List<OnlineOrder> findByNameContainingIgnoreCaseOrPhoneContaining(@Param("keyword") String keyword);
    List<OnlineOrder> findByConfirm(int confirm);

    List<OnlineOrder> findByDeliveryStatus(int deliveryStatus);
    OnlineOrder findByOnOrderID(Long onOrderID);

}
