package vn.cosbeauty.service;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }
    public List<Product> searchProducts(String query) {
        return productRepository.findProductsByProductName(query);
    }
    public List<Product> searchProductsContainingIgnoreCase(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }


    public Product getProductById(Long productID) {
        return productRepository.findById(productID).orElse(null);
    }
    public List<Product> getRelatedProducts(Integer categoryId) {
        return productRepository.findTop4ByCategoryId(categoryId);
    }
}