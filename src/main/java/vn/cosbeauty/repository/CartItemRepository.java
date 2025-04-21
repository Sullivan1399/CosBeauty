package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.cosbeauty.entity.CartID;
import vn.cosbeauty.entity.CartItem;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartID> {
    // Phương thức này sử dụng CartID làm key, vì vậy không cần thay đổi gì ở đây
    List<CartItem> findByCustomerID(Long customerID);
}