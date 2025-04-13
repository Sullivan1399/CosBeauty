package vn.cosbeauty.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> searchProducts(String query) {
        return productRepository.findProductsByProductName(query);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}