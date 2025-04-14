package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Product;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductsByProductID(Long productID);
    List<Product> findProductsByProductName(String productName);
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    List<Product> findAll();
}
