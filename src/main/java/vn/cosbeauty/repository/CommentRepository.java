package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.Comment;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    boolean existsByCustomerAndProduct(Customer customer, Product product);
    List<Comment> findByCustomer_CustomerIDAndProduct_ProductIDIn(Long customerId, List<Long> productIds);
}