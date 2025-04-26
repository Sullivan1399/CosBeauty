package vn.cosbeauty.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Product;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
	Page<Product> findProductsByProductName(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(p.category.cateName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Product> getProductByCateName(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(p.supplier.supName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Product> getProductBySupName(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p.category.cateName FROM Product p WHERE p.id = :productID")
    List<String> getProductCategory(@Param("productID") Long productID);
    @Query("SELECT p.supplier.supName FROM Product p WHERE p.id = :productID")
    List<String> getProductSupplier(@Param("productID") Long productID);
    @Query("SELECT COUNT(*) FROM Product p WHERE p.quantity=0")
    Long countProduct_outOfStock();
    @Query("SELECT p FROM Product p WHERE p.quantity=0 ")
    Page<Product> getListProduct_outOfStock(Pageable pageable);
    @Query("SELECT COUNT(*) FROM Product p WHERE p.quantity>0")
    Long countProduct_inStock();
    @Query("SELECT p FROM Product p WHERE p.quantity>0 ")
    Page<Product> getLisProduct_inStock(Pageable pageable);
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    @Query("SELECT p FROM Product p WHERE p.category.catID = ?1")
    List<Product> findTop4ByCategoryId(Integer categoryId);
    Product findProductByProductID(Long productID);
}
