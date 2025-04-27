package vn.cosbeauty.repository;

import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.cosbeauty.entity.Comment;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.OnlineOrder;
import vn.cosbeauty.entity.Product;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    long countByCustomerAndOnlineOrder(Customer customer, OnlineOrder onlineOrder);
    List<Comment> findByCustomer_CustomerIDAndProduct_ProductIDIn(Long customerId, List<Long> productIds);
    List<Comment> findByProduct(Product product);
    long countByCustomerAndProductAndOnlineOrder(Customer customer, Product product, OnlineOrder onlineOrder);
    @Query("SELECT c FROM Comment c WHERE c.customer = :customer AND c.product = :product AND c.onlineOrder = :order")
    List<Comment> findByCustomerAndProductAndOnlineOrder(@Param("customer") Customer customer,
                                                         @Param("product") Product product,
                                                         @Param("order") OnlineOrder order);
}