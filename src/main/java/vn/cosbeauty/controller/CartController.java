package vn.cosbeauty.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.CartID;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CartService;
import vn.cosbeauty.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        System.out.println("Tìm kiếm với từ khóa: " + keyword);
        return productService.searchProductsContainingIgnoreCase(keyword);
    }
    @PostMapping("/add")
    public String addToCart(@RequestParam("customerId") Long customerId,
                            @RequestParam("productId") Long productId) {

        cartService.addToCart(customerId, productId);

        cartService.addToCart(customerId, productId);
        return String.valueOf(ResponseEntity.ok("Added to cart"));  // Chuyển hướng tới giỏ hàng
    }


    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam Long customerID) {
        List<CartItem> cartItems = cartService.getCartItemsByCustomerID(customerID);
        return ResponseEntity.ok(cartItems);
    }
}

