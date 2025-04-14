package vn.cosbeauty.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.CartRepository;
import vn.cosbeauty.repository.ProductRepository;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    public void addToCart(Long productId, Customer customer) {
        Product product = productRepository.findProductsByProductID(productId);

        CartItem existingCartItem = cartRepository.findByCustomerAndProduct(customer, product);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            cartRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem(1, customer, product);
            newCartItem.setCustomerID(customer.getCustomerID());
            newCartItem.setProductID(product.getProductID());
            cartRepository.save(newCartItem);
        }
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

