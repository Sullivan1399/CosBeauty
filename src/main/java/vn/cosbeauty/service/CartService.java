package vn.cosbeauty.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import vn.cosbeauty.entity.CartID;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.CartItemRepository;
import vn.cosbeauty.repository.CartRepository;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    public void addToCart(Long customerId, Long productId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartID cartID = new CartID(customerId, productId);

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Optional<CartItem> existingCartItem = cartItemRepository.findById(cartID);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);  // Tăng số lượng
            cartItemRepository.save(cartItem);  // Lưu lại thay đổi
        } else {
            CartItem newCartItem = new CartItem(cartID, 1);  // Tạo mới giỏ hàng với số lượng là 1
            cartItemRepository.save(newCartItem);  // Lưu vào repository
        }
    }
    public Optional<CartItem> findByCartID(Long customerID, Long productID){
        CartID cartID = new CartID(customerID, productID);
        return cartItemRepository.findById(cartID);

    }
    public CartItem findByCustomerAndProduct(Customer customer, Product product) {
        return cartRepository.findByCustomerAndProduct(customer, product);
    }
    public List<CartItem> getAllCartItems() {
        return cartRepository.findAll();
    }
    public List<CartItem> getCartItemsByCustomerID(Long customerID) {
        return cartRepository.findByCustomerID(customerID);
    }

    public void removeFromCart(Long id) {
        cartRepository.deleteById(id);
    }

    public CartItem incrementQuantity(Long id) {
        CartItem item = cartRepository.findById(id).orElseThrow();
        item.setQuantity(item.getQuantity() + 1);
        return cartRepository.save(item);
    }
}

