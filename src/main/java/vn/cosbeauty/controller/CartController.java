package vn.cosbeauty.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CartService;
import vn.cosbeauty.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("api/cart")
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
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            return "redirect:/login";
        }

        cartService.addToCart(productId, customer);

        System.out.println("Product ID: " + productId);
        return "redirect:/shoping-cart";
    }


    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam Long customerID) {
        List<CartItem> cartItems = cartService.getCartItemsByCustomerID(customerID);
        return ResponseEntity.ok(cartItems);
    }
}

