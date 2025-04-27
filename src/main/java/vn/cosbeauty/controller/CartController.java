package vn.cosbeauty.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CartService;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.CustomerService;
import vn.cosbeauty.service.ProductService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/update")
    public String updateCart(@RequestParam Map<String, String> quantities,
                             @RequestParam(required = false) String redirect) {
        Long customerId = customerService.getCurrentCustomerID();

        for (Map.Entry<String, String> entry : quantities.entrySet()) {
            String key = entry.getKey();

            // Lọc key và lấy productId bằng cách bỏ qua "quantities[...]" phần trong key
            if (key.matches("quantities\\[(\\d+)\\]")) {
                String productIdStr = key.replaceAll("quantities\\[(\\d+)\\]", "$1");  // Lấy productId từ key
                Long productId = Long.parseLong(productIdStr);
                int quantity = Integer.parseInt(entry.getValue());

                System.out.println("Customer ID: " + customerId + " - Product ID: " + productId + " - Quantity: " + quantity);
                cartService.updateCartItem(customerId, productId, quantity);
            }
        }

        return "redirect:/" + (redirect != null ? redirect : "cart");
    }

    @PostMapping("/goCheckout")
    public String goCheckout(@RequestParam Map<String, String> quantities,
                             @RequestParam(required = false) String redirect) {
        Long customerId = customerService.getCurrentCustomerID();

        for (Map.Entry<String, String> entry : quantities.entrySet()) {
            String key = entry.getKey();

            // Lọc key và lấy productId bằng cách bỏ qua "quantities[...]" phần trong key
            if (key.matches("quantities\\[(\\d+)\\]")) {
                String productIdStr = key.replaceAll("quantities\\[(\\d+)\\]", "$1");  // Lấy productId từ key
                Long productId = Long.parseLong(productIdStr);
                int quantity = Integer.parseInt(entry.getValue());
                cartService.updateCartItem(customerId, productId, quantity);
            }
        }

        return "redirect:/checkout";
    }


    @Component("formatter")
    public class PriceFormatter {
        public String formatPrice(double price) {
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            return currencyFormat.format(price) + " ₫";
        }
    }
    @GetMapping("/view")
    public String showCart(Model model) {
        Long customerId = customerService.getCurrentCustomerID();
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(customerId);
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> {
                    BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice());
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    BigDecimal discountPercent = BigDecimal.valueOf( item.getProduct().getDiscount() );
                    BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100)));

                    return price.multiply(quantity).multiply(discountMultiplier);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "web/shoping-cart";
    }
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        System.out.println("Tìm kiếm với từ khóa: " + keyword);
        return productService.searchProductsContainingIgnoreCase(keyword);
    }
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Long customerid = customerService.getCurrentCustomerID();
        cartService.addToCart(customerid, id);
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng!");
        return "redirect:/";
    }


    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam Long customerID) {
        List<CartItem> cartItems = cartService.getCartItemsByCustomerID(customerID);
        return ResponseEntity.ok(cartItems);
    }
    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long productID, RedirectAttributes attributes) {
        Long customerID = customerService.getCurrentCustomerID();
        boolean removed = cartService.removeCartItem(customerID, productID);

        if (removed) {
            attributes.addFlashAttribute("message", "Sản phẩm đã được xóa khỏi giỏ hàng thành công");
        } else {
            attributes.addFlashAttribute("error", "Không tồn tại sản phẩm trong xóa hàng");
        }
        return "redirect:/api/cart/view";

    }

}

