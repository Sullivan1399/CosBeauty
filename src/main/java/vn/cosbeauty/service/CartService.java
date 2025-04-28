package vn.cosbeauty.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Transactional
    public void addToCart(Long customerId, Long productId, int quantity) {
        // Tìm sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + productId));

        // Kiểm tra số lượng tồn kho
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Số lượng yêu cầu vượt quá tồn kho: " + product.getQuantity());
        }

        // Tạo CartID để tìm CartItem
        CartID cartID = new CartID(customerId, productId);
        Optional<CartItem> existingItem = cartItemRepository.findById(cartID);

        if (existingItem.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, tăng số lượng
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (newQuantity > product.getQuantity()) {
                throw new IllegalArgumentException("Tổng số lượng vượt quá tồn kho: " + product.getQuantity());
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Nếu sản phẩm chưa có, tạo mới CartItem
            CartItem newItem = new CartItem();
            newItem.setCustomerID(customerId);
            newItem.setProductID(productId);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }
    public void clearCartByCustomerId(Long customerId) {
        cartItemRepository.deleteCartByCustomerId(customerId);
    }
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

    public void addReOrderToCart(Long customerId, Long productId,int quantity) {
        if (quantity <= 0) {
            return; // Ignore invalid quantities
        }

        CartID cartID = new CartID(customerId, productId);
        Optional<CartItem> existingItem = cartItemRepository.findById(cartID);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductID(productId);
            newItem.setCustomerID(customerId);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

    }
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

    @Transactional
    public boolean removeCartItem(Long customerID, Long productID) {
        CartID cartID = new CartID(customerID, productID);
        if (cartItemRepository.existsById(cartID)) {
            cartItemRepository.deleteById(cartID);
            return true;
        }
        return false;
    }

}

