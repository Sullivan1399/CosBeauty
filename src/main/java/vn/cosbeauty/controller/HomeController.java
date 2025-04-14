package vn.cosbeauty.controller;

import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.ProductService;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping({"/home","/"})
    public String getAll(Model model) {
        List<Category> categories = categoryService.getCategories();

        List<Product> products = productService.getAllProduct();
//        for (Product p : products) {
//            System.out.println("- " + p.getImageUrl() + " | " + p.getProductName());
//        }
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "web/index";
    }
    @GetMapping("/instore")
    public String instore() {
        return "web/instore";
    }

 
    @GetMapping({"/shoping-cart"})
    public String cart() {
        return "web/shoping-cart";
    }
    @GetMapping({"/index"})
    public String index() {
        return "web/index";
    }
}
