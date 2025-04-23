package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }
    
    public List<Category> findCategoryByCateName(Category category) {
    	return categoryRepository.findCategoryByCateName(category.getCateName());
    }
    
    public Category findByID(int catID) {
    	Category category = categoryRepository.findById(catID)
    		    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    	return category;
    }
}
