package vn.cosbeauty.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import vn.cosbeauty.entity.Product;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Product> findProductsByProductName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.cateName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Product> getProductByCateName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.supplier.supName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Product> getProductBySupName(@Param("keyword") String keyword, Pageable pageable);

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

    @Query("SELECT p FROM Product p " +
            "WHERE (:supplierID IS NULL OR p.supplier.supID = :supplierID) " +
            "AND (:categoryID IS NULL OR p.category.catID = :categoryID)")
    Page<Product> findProducts(
            @Param("supplierID") Integer supplierID,
            @Param("categoryID") Long categoryID,
            Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.category.catID = :categoryId")
    Page<Product> getProductByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.supplier.supID = :supplierID")
    Page<Product> getProductBySupplier(@Param("supplierID") Long supplierID, Pageable pageable);
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.onOrderDetails od " +
            "LEFT JOIN p.offOrderDetails ofd " +
            "GROUP BY p " +
            "ORDER BY SUM(od.quantity) + SUM(ofd.quantity) DESC")
    List<Product> findTopSellingProducts();
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);
}
