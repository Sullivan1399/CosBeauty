package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    CartItem findByProductID(Long productId);
    CartItem findByCustomerAndProduct(Customer customer, Product product);
    List<CartItem> findByCustomerID(Long customerID);

}

