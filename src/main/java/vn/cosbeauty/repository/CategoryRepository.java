package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;

import java.util.List;

public interface  CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAll();
}
