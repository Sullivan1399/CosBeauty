package vn.cosbeauty.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.cosbeauty.entity.Category;

public interface  CategoryRepository extends JpaRepository<Category, Integer> {
	Optional<Category> findByCateNameIgnoreCase(String cateName);
}
