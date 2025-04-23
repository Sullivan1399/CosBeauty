package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.CustomerService;
import vn.cosbeauty.service.ProductService;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/")
    public String getAll(Model model, 
//    		@RequestParam(value = "logout", required = false) String logout,
    		@RequestParam(value = "keyword",defaultValue = "") String keyword,
            @RequestParam(value="page", required=false, defaultValue="1") int page,
            @RequestParam(value="listCategory",required = false) List<String> listCate,
            @RequestParam(value="listSupplier",required = false) List<String> listSup) {
//    	if (logout != null) {
//            model.addAttribute("message", "Bạn đã đăng xuất thành công!");
//            System.out.println("Đang Logout neh ba!");
//        }
    	List<Category> categories = categoryService.getAllCategory();
    	Page<Product> products = productService.getProductHome(page, 10);

//        List<Product> products = productService.getAllProduct();
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "web/index";
    }
    @GetMapping("/shop-grid")
    public String shopGrid(Model model, 
    		@RequestParam(value="page", required=false, defaultValue="1") int page) {
        List<Category> categories = categoryService.getAllCategory();

        Page<Product> products = productService.getProductHome(page, 10);
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
