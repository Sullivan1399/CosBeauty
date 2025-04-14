package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.ProductService;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

//    @GetMapping("/search")
//    public String searchProducts(@RequestParam("query") String query, Model model) {
//        model.addAttribute("products", productService.searchProducts(query));
//        return "searchResults";
//    }

    @GetMapping("/search2")
    public String searchProductsByCaseIgnore(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchProductsContainingIgnoreCase(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword); // hiển thị lại trong ô tìm kiếm nếu cần
        return "web/search-results"; // ví dụ bạn tạo file search-results.html
    }


}
