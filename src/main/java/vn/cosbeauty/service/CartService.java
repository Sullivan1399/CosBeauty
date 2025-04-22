package vn.cosbeauty.service;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.cosbeauty.entity.CartID;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.CartItemRepository;
import vn.cosbeauty.repository.CartRepository;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.repository.ProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    public List<CartItem> getCartItemsByCustomerId(Long customerId) {
        return cartItemRepository.findByCustomerID(customerId);
    }
    public void updateCartItem(Long customerId, Long productId, int quantity) {
        CartID cartID = new CartID(customerId, productId);
        Optional<CartItem> optionalItem = cartItemRepository.findById(cartID);

        if (optionalItem.isPresent()) {
            if (quantity > 0) {
                CartItem item = optionalItem.get();
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            } else {
                cartItemRepository.deleteById(cartID); // Nếu số lượng <= 0 thì xóa luôn
            }
        } else if (quantity > 0) {
            CartItem newItem = new CartItem();
            newItem.setProductID(productId);
            newItem.setCustomerID(customerId);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    // Hàm tính tổng giá trị giỏ hàng (nếu cần)

    public void addToCart(Long customerId, Long productId) {

        CartID cartID = new CartID(customerId, productId);
        Optional<CartItem> existingItem = cartItemRepository.findById(cartID);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductID(productId);
            newItem.setCustomerID(customerId);
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
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

