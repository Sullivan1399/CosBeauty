package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.cosbeauty.service.ProductService;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        model.addAttribute("products", productService.searchProducts(query));
        return "searchResults";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Long productId) {
        // Thêm sản phẩm vào giỏ hàng
        return "redirect:/cart";
    }
}
