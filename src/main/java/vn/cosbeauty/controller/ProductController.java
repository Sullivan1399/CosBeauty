package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.service.CategoryService;
import vn.cosbeauty.service.FileService;
import vn.cosbeauty.service.ProductService;
import vn.cosbeauty.service.SupplierService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    //    @GetMapping("/search")
//    public String searchProducts(@RequestParam("query") String query, Model model) {
//        model.addAttribute("products", productService.searchProducts(query));
//        return "searchResults";
//    }
    @GetMapping("/admin/ManageProducts")
    public String manageProducts(Model model, 
    		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
    		HttpServletRequest request) {
    	Page<Product> products = productService.getAllproduct(page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
    	model.addAttribute("products", productOfCurrentPage);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	model.addAttribute("totalProduct", productService.totalProduct());
    	model.addAttribute("countInStock",productService.countProductInStock());
        model.addAttribute("countOutOfStock",productService.countProductOutOfStock());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI());
        Product p = new Product();
    	return "admin/manage-products";
    }
   
    @GetMapping("/admin/createProduct")
    public String showFormCreateProduct(Model model) {
    	Product product = new Product();
    	model.addAttribute("product", product);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	return "admin/add-product";
    }
    
    @GetMapping("/admin/editProduct/{id}")
    public String showFormUpdateProduct(@PathVariable(value = "id") long id, Model model) {
    	Product product = productService.getProductById(id);
    	model.addAttribute("product", product);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	return "admin/update-product";
    }
    
    @GetMapping("/admin/ManageProducts/inStock")
    public String manageProductsInStock(Model model, 
    		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
    		HttpServletRequest request) {
    	Page<Product> products = productService.getProductInStock(page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
    	model.addAttribute("products", productOfCurrentPage);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	model.addAttribute("totalProduct", productService.totalProduct());
    	model.addAttribute("countInStock",productService.countProductInStock());
        model.addAttribute("countOutOfStock",productService.countProductOutOfStock());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI());
    	return "admin/manage-products";
    }
    
    @GetMapping("/admin/ManageProducts/outOfStock")
    public String manageProductsOutStock(Model model, 
    		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
    		HttpServletRequest request) {
    	Page<Product> products = productService.getProductOutOfStock(page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
    	model.addAttribute("products", productOfCurrentPage);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	model.addAttribute("totalProduct", productService.totalProduct());
    	model.addAttribute("countInStock",productService.countProductInStock());
        model.addAttribute("countOutOfStock",productService.countProductOutOfStock());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI());
        Product p = new Product();
    	return "admin/manage-products";
    }
    
    @GetMapping("/admin/searchByName")
    public String searchProductByName(Model model,
    		@RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value="page", required=false, defaultValue="1") int page,
            HttpServletRequest request) {
    	Page<Product> products = productService.searchProducts(keyword, page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
    	model.addAttribute("products", productOfCurrentPage);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	model.addAttribute("totalProduct", productService.totalProduct());
    	model.addAttribute("countInStock",productService.countProductInStock());
        model.addAttribute("countOutOfStock",productService.countProductOutOfStock());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI()+"?keyword="+keyword);
    	return "admin/manage-products";
    }
    
    @GetMapping("/admin/searchProductByCategory")
    public String searchProductByCategory(Model model,
                                      @RequestParam(value = "category") String category,
                                      @RequestParam(value="page", required=false, defaultValue="1") int page,
                                      HttpServletRequest request) {
    	Page<Product> products = productService.getProductByCateName(category, page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
    	model.addAttribute("products", productOfCurrentPage);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	model.addAttribute("totalProduct", productService.totalProduct());
    	model.addAttribute("countInStock",productService.countProductInStock());
        model.addAttribute("countOutOfStock",productService.countProductOutOfStock());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI()+"?category="+category);
    	return "admin/manage-products";
    }
    
    @GetMapping("/admin/searchProductBySupplier")
    public String searchProductBySupplier(Model model,
                                      @RequestParam(value = "supplier") String supplier,
                                      @RequestParam(value="page", required=false, defaultValue="1") int page,
                                      HttpServletRequest request) {
    	Page<Product> products = productService.getProductBySupName(supplier, page, 10);
    	List<Product> productOfCurrentPage = products.getContent();
    	model.addAttribute("products", productOfCurrentPage);
    	model.addAttribute("listCategory", categoryService.getAllCategory());
    	model.addAttribute("listSupplier", supplierService.getAllSupplier());
    	model.addAttribute("totalProduct", productService.totalProduct());
    	model.addAttribute("countInStock",productService.countProductInStock());
        model.addAttribute("countOutOfStock",productService.countProductOutOfStock());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("requestURI", request.getRequestURI()+"?supplier="+supplier);
    	return "admin/manage-products";
    }
    
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        List<Category> categories = categoryService.getAllCategory();
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
    
 
    @PostMapping("/admin/addProduct")
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("imageInput") MultipartFile imageFile,
                             @RequestParam("catID") int catID,
                             @RequestParam("supID") int supID) {
    	if (!imageFile.isEmpty()) {
    	    String image = fileService.upload(imageFile, "media");
    	    product.setImageUrl(image);
    	}
        Category category = categoryService.findByID(catID);
        Supplier supplier = supplierService.findByID(supID);
        product.setCategory(category);
        product.setSupplier(supplier);
        productService.addProduct(product);
        return "redirect:/admin/ManageProducts";
    }
    
    @PostMapping("/admin/updateProduct")
    public String updateProduct(@ModelAttribute("product") Product product,
    							@RequestParam("imageInput") MultipartFile imageFile,
    							@RequestParam("catID") int catID,
                                @RequestParam("supID") int supID) {
    	if (!imageFile.isEmpty()) {
            String image = fileService.upload(imageFile,"media");
            product.setImageUrl(image);
        }
    	Category category = categoryService.findByID(catID);
        Supplier supplier = supplierService.findByID(supID);
        product.setCategory(category);
        product.setSupplier(supplier);
        productService.updateProduct(product);
    	return "redirect:/admin/ManageProducts";
    }
}
