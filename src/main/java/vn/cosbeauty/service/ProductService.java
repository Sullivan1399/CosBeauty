package vn.cosbeauty.service;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> getProductHome(int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
    	return productRepository.findAll(pageable);
    }
    
    public Page<Product> getAllproduct(int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
    	return productRepository.findAll(pageable);
    }
    
    public Product getProductById(Long productID) {
        return productRepository.findById(productID).orElse(null);
    }
    
    public Page<Product> getProductByCateName(String cateName, int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
    	return productRepository.getProductByCateName(cateName, pageable);
    }
    
    public Page<Product> getProductBySupName(String supName, int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
    	return productRepository.getProductBySupName(supName, pageable);
    }
    
    public void addProduct(Product product) {
    	productRepository.save(product);
    }
    
    public void updateProduct(Product product) {
    	productRepository.save(product);
    }
    public Page<Product> searchProducts(String keyword, int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
        return productRepository.findProductsByProductName(keyword,pageable);
    }
    
    public List<Product> searchProductsContainingIgnoreCase(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    public List<Product> getRelatedProducts(Integer categoryId) {
        return productRepository.findTop4ByCategoryId(categoryId);
    }
    
    public long totalProduct() {
    	return productRepository.count();
    }
    
    public long countProductInStock() {
    	return productRepository.countProduct_inStock();
    }
    
    public long countProductOutOfStock() {
    	return productRepository.countProduct_outOfStock();
    }
    
    public Page<Product> getProductInStock(int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
    	return productRepository.getLisProduct_inStock(pageable);
    }
    
    public Page<Product> getProductOutOfStock(int page, int size) {
    	Pageable pageable = PageRequest.of(page-1, size);
    	return productRepository.getListProduct_outOfStock(pageable);
    }
    
    public List<String> getProductCategory(Long productID) {
    	return productRepository.getProductCategory(productID);
    }
    
    public List<String> getProductSupplier(Long productID) {
    	return productRepository.getProductSupplier(productID);
    }
}