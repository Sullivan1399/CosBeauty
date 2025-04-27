package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CartService;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.CustomerService;
import vn.cosbeauty.service.EmployeeService;
import vn.cosbeauty.service.ProductService;
import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.service.*;


import java.math.BigDecimal;
import java.util.Collections;
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
    @Autowired
    private SupplierService supplierService;

    @GetMapping("/")
    public String getAll(Model model,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            @RequestParam(value="page", required=false, defaultValue="1") int page,
            @RequestParam(value="listCategory",required = false) List<String> listCate,
            @RequestParam(value="listSupplier",required = false) List<String> listSup) {
        	if (logout != null) {
        		model.addAttribute("message", "Bạn đã đăng xuất thành công!");
        		System.out.println("Đang Logout neh ba!");
        	}
	        List<Category> categories = categoryService.getAllCategory();
	        Page<Product> products = productService.getProductHome(page, 10);
	        List<CartItem> cartItems;
	        BigDecimal totalAmount = BigDecimal.ZERO;

	        if (customerService.isCustomer()) {
	            Long id = customerService.getCurrentCustomerID();
	            cartItems = cartService.getCartItemsByCustomerId(id);
                 totalAmount = cartItems.stream()
                        .map(item -> {
                            BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice());
                            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                            BigDecimal discountPercent = BigDecimal.valueOf( item.getProduct().getDiscount() );
                            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100)));

                            return price.multiply(quantity).multiply(discountMultiplier);
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
	        } else {
	            cartItems = Collections.emptyList();
	        }

	        model.addAttribute("cartItems", cartItems);
	        model.addAttribute("totalAmount", totalAmount);
	        model.addAttribute("categories", categories);
	        model.addAttribute("products", products);
	        return "web/index";
    }

    @GetMapping("/web/shop-grid")
    public String shopGrid(Model model,
    		@RequestParam(value="page", required=false, defaultValue="1") int page,
    		HttpServletRequest request) {
    	List<Category> categories = categoryService.getAllCategory();
    	Page<Product> products = productService.getProductHome(page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
        List<Supplier> suppliers = supplierService.getAllSupplier();
        List<Product> topSellingProducts = productService.getTopSellingProducts();


        // Truyền danh sách sản phẩm vào model để hiển thị trong view
        model.addAttribute("topSellingProducts", topSellingProducts);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("categories", categories);
        model.addAttribute("products", productOfCurrentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI());
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
