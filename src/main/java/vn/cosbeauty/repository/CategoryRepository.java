package vn.cosbeauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.cosbeauty.entity.Category;

public interface  CategoryRepository extends JpaRepository<Category, Integer> {
	@Query("SELECT c FROM Category c WHERE LOWER(c.cateName) LIKE LOWER(CONCAT('%',:keyword,'%'))")
	List<Category> findCategoryByCateName(@Param("keyword") String keyword);
    
}
