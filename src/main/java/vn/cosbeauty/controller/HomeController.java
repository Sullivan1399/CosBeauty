package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CartService;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.CustomerService;
import vn.cosbeauty.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CartService cartService;

    @GetMapping({"/"})
    public String getAll(Model model, @RequestParam(value = "logout", required = false) String logout) {
    	if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công!");
        }
    	List<Category> categories = categoryService.getCategories();

        List<Product> products = productService.getAllProduct();
        Long id = customerService.getCurrentCustomerID();
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(id);
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng giá trị
        model.addAttribute("categories", categories);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("products", products);
        return "web/index";
    }
    @GetMapping("/shop-grid")
    public String shopGrid(Model model, @RequestParam(value = "logout", required = false) String logout) {
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công!");
        }
        List<Category> categories = categoryService.getCategories();

        List<Product> products = productService.getAllProduct();
        Long customerId = 1L; // hoặc lấy từ session, user login
        model.addAttribute("customerId", customerId);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "web/shop-grid";
    }
    @GetMapping("/instore")
    public String instore() {
        return "web/instore";
    }


    @GetMapping({"/shoping-cart"})
    public String cart() {
        return "web/shoping-cart";
    }
    @GetMapping({"/shop-details"})
    public String shopDetails() {
        return "web/shop-details";
    }

}
