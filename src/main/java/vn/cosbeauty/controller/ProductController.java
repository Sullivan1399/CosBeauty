package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    //    @GetMapping("/search")
//    public String searchProducts(@RequestParam("query") String query, Model model) {
//        model.addAttribute("products", productService.searchProducts(query));
//        return "searchResults";
//    }
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        List<Category> categories = categoryService.getCategories();
        Product product = productService.getProductById(id);

        List<Product> relatedProducts = productService.getRelatedProducts(product.getCategory().getCatID());
        List<Product> top4RelatedProducts = relatedProducts.stream().limit(4).collect(Collectors.toList()); // Lấy 4 sản phẩm đầu tiên
        model.addAttribute("categories", categories);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("relatedProducts", top4RelatedProducts);
        model.addAttribute("product", product);
        return "web/shop-details";
    }

    @GetMapping("/search2")
    public String searchProductsByCaseIgnore(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchProductsContainingIgnoreCase(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword); // hiển thị lại trong ô tìm kiếm nếu cần
        return "web/search-results"; // ví dụ bạn tạo file search-results.html
    }
}
