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
    
    public Category findByID(int catID) {
    	Category category = categoryRepository.findById(catID)
    		    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    	return category;
    }
    
    public boolean isCategoryExists(String cateName) {
    	return categoryRepository.findByCateNameIgnoreCase(cateName).isPresent();
    }
    
    public void addCategory(Category category) {
    	categoryRepository.save(category);
    }
    
    public void updateCategory(Category category) {
    	categoryRepository.save(category);
    }
}
