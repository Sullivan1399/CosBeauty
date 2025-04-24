package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.service.CategoryService;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
//	@GetMapping("/admin/createCategory")
//	public String addCategory() {
//		
//	}
	@PostMapping("/admin/addCategory")
	public String addCategory(@RequestParam("cateName") String categoryName,
								RedirectAttributes redirectAttributes,
								HttpServletRequest request) {
		if (categoryService.isCategoryExists(categoryName)) {
	        redirectAttributes.addFlashAttribute("message", "Category đã tồn tại.");
	    } else {
	        categoryService.addCategory(new Category(categoryName));
	        redirectAttributes.addFlashAttribute("message", "Thêm category thành công.");
	    }

	    String referer = request.getHeader("Referer");
	    return "redirect:" + (referer != null ? referer : "/admin/ManageProducts");
	}
}
