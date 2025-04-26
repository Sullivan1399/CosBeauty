package vn.cosbeauty.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.service.SupplierService;
@Controller
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @GetMapping("/admin/manage-supplier")
    public String viewSuppliers(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        // Kích thước mỗi trang
        int pageSize = 10;

        // Lấy danh sách nhà cung cấp với phân trang và tìm kiếm
        Page<Supplier> supplierPage = supplierService.getSuppliers(page, pageSize, keyword);

        // Thêm dữ liệu vào model
        model.addAttribute("suppliers", supplierPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", supplierPage.getTotalPages());
        model.addAttribute("keyword", keyword);  // Đưa từ khóa tìm kiếm vào model để hiển thị lại trên form tìm kiếm

        return "admin/manage-supplier";
    }
    @GetMapping("/admin/manage-supplier/update")
    public String showUpdateForm(@RequestParam("id") int id, Model model) {
        // Lấy thông tin nhà cung cấp theo ID
        Supplier supplier = supplierService.getSupplierById(id);

        // Thêm nhà cung cấp vào model để hiển thị trên form
        model.addAttribute("supplier", supplier);

        return "admin/update-supplier";
    }

    @PostMapping("/admin/manage-supplier/update")
    public String updateSupplier(@Valid @ModelAttribute("supplier") Supplier supplier,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi nhập liệu
        if (result.hasErrors()) {
            return "admin/update-supplier";
        }

        try {
            // Cập nhật thông tin nhà cung cấp
            supplierService.updateSupplier(supplier);
            redirectAttributes.addFlashAttribute("success", "Cập nhật nhà cung cấp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật nhà cung cấp thất bại: " + e.getMessage());
        }

        // Chuyển hướng về trang quản lý nhà cung cấp
        return "redirect:/admin/manage-supplier";
    }
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
