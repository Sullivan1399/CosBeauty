package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.service.SupplierService;

import java.util.List;

@Controller
public class SuplpierController {
	@Autowired
	private SupplierService supplierService;
	
	@PostMapping("/admin/addSupplierMain")
	public String addSupplier(@RequestParam("supName") String supplierName, RedirectAttributes redirectAttributes) {
		if (supplierService.isCategoryExists(supplierName)) {
			redirectAttributes.addFlashAttribute("message", "Category đã tồn tại.");
            System.out.println("Category đã tồn tại.");
            return "redirect:/admin/ManageProducts";
        }
//        supplierService.addSupplier(new Supplier(supplierName));
        return "redirect:/admin/ManageProducts";
	}
}
