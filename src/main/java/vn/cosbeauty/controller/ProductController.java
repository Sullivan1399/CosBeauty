package vn.cosbeauty.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Comment;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.service.*;

import java.util.ArrayList;
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
    @Autowired
    private CommentService commentService;

    @GetMapping("/products/category/{categoryId}")
    public String getProductsByCategory(@PathVariable Long categoryId,
                                        Model model,
                                        @RequestParam(value="page", required=false, defaultValue="1") int page,
                                        HttpServletRequest request) {
        // Lấy danh sách sản phẩm theo category với phân trang
        page = page - 1;
        Page<Product> productPage = productService.findProductsByCategory(categoryId, PageRequest.of(page, 10));

        // Tổng số trang
        int totalPage = productPage.getTotalPages();
        // Trang hiện tại
        int currentPage = productPage.getNumber()+ 1;
        List<Category> categories = categoryService.getAllCategory();
        List<Product> productOfCurrentPage = productPage.getContent();
        List<Supplier> suppliers = supplierService.getAllSupplier();

        List<Product> topSellingProducts = productService.getTopSellingProducts();

        // Truyền danh sách sản phẩm vào model để hiển thị trong view
        model.addAttribute("topSellingProducts", topSellingProducts);

        model.addAttribute("productPage", productPage);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("categories", categories);
        model.addAttribute("products", productOfCurrentPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("requestURI", request.getRequestURI());  // Để tạo liên kết phân trang

        return "web/shop-grid";
    }
    @GetMapping("/products/supplier/{supplierId}")
    public String getProductsBySupplier(@PathVariable Long supplierId,
                                        Model model,
                                        @RequestParam(value="page", required=false, defaultValue="1") int page,
                                        HttpServletRequest request) {
        // Lấy danh sách sản phẩm của nhà cung cấp với phân trang
        Page<Product> productPage = productService.findProductsBySupplier(supplierId, PageRequest.of(page - 1, 10));

        // Tổng số trang
        int totalPage = productPage.getTotalPages();
        // Trang hiện tại
        int currentPage = productPage.getNumber() + 1;  // Tính lại currentPage cho phù hợp với Thymeleaf

        List<Category> categories = categoryService.getAllCategory();
        List<Product> productOfCurrentPage = productPage.getContent();

        List<Supplier> suppliers = supplierService.getAllSupplier();

        List<Product> topSellingProducts = productService.getTopSellingProducts();

        // Truyền danh sách sản phẩm vào model để hiển thị trong view
        model.addAttribute("topSellingProducts", topSellingProducts);

        model.addAttribute("productPage", productPage);
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("categories", categories);
        model.addAttribute("products", productOfCurrentPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("requestURI", request.getRequestURI());  // Để tạo liên kết phân trang

        return "web/shop-grid";
    }


    @GetMapping("/admin/ManageProducts")
    public String manageProducts(Model model, 
    		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
    		HttpServletRequest request) {
    	Page<Product> products = productService.getProductHome(page, 10);
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
    
    @GetMapping("/web/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        List<Category> categories = categoryService.getAllCategory();
        Product product = productService.getProductById(id);
        if (product == null) {
            model.addAttribute("error", "Sản phẩm không tồn tại");
            return "error-page";
        }
        List<Comment> comments = commentService.getCommentsForProduct(product);
        if (comments == null) {
            comments = new ArrayList<>();
        }
        double avgRating = commentService.calculateAverageRating(product);

        model.addAttribute("ratings", comments.stream()
                .map(comment -> comment.getRate())
                .collect(Collectors.toList()));
        List<Double> ratingProgressList = new ArrayList<>();
        for (Comment comment : comments) {
            double ratingProgress = (comment.getRate() / 5.0) * 100;
            ratingProgressList.add(ratingProgress);
        }
        long reviewCount = comments.size();

        Comment comment = new Comment();
        comment.setProduct(product); // Gán productID vào comment (nếu cần)
        // Lấy tất cả các bình luận
        List<Comment> bl = commentService.getAll();

// Khởi tạo các biến để đếm số sao
        int oneStarCount = 0;
        int twoStarCount = 0;
        int threeStarCount = 0;
        int fourStarCount = 0;
        int fiveStarCount = 0;

        int totalComments = bl.size();

        for (Comment c : bl) {
            int rate = c.getRate();
            switch (rate) {
                case 1:
                    oneStarCount++;
                    break;
                case 2:
                    twoStarCount++;
                    break;
                case 3:
                    threeStarCount++;
                    break;
                case 4:
                    fourStarCount++;
                    break;
                case 5:
                    fiveStarCount++;
                    break;
                default:
                    break;
            }
        }

        double oneStarPercentage = (double) oneStarCount / totalComments * 100;
        double twoStarPercentage = (double) twoStarCount / totalComments * 100;
        double threeStarPercentage = (double) threeStarCount / totalComments * 100;
        double fourStarPercentage = (double) fourStarCount / totalComments * 100;
        double fiveStarPercentage = (double) fiveStarCount / totalComments * 100;

        model.addAttribute("fiveStarPercentage", fiveStarPercentage);
        model.addAttribute("fourStarPercentage", fourStarPercentage);
        model.addAttribute("threeStarPercentage", threeStarPercentage);
        model.addAttribute("twoStarPercentage", twoStarPercentage);
        model.addAttribute("oneStarPercentage", oneStarPercentage);
        // Truyền đối tượng comment vào model
        model.addAttribute("comment", comment);
        model.addAttribute("reviewCount", reviewCount);

        // Truyền vào model
        model.addAttribute("product", product);
        model.addAttribute("comments", comments);
        model.addAttribute("avg_rating", avgRating);
        model.addAttribute("ratingProgressList", ratingProgressList);
        List<Product> relatedProducts = productService.getRelatedProducts(product.getCategory().getCatID());
        List<Product> top4RelatedProducts = relatedProducts.stream().limit(4).collect(Collectors.toList()); // Lấy 4 sản phẩm đầu tiên
        model.addAttribute("categories", categories);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("relatedProducts", top4RelatedProducts);
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
