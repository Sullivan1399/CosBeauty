package vn.cosbeauty.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.repository.*;
import vn.cosbeauty.service.ImportOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/employee/import-orders")
public class ImportOrderController {

    @Autowired
    private ImportOrderService importOrderService;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("suppliers", supplierRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "web/create-import-order"; // tên file HTML
    }

    @PostMapping("/create")
    public String saveImportOrder(
            @RequestParam Long supplierId,
            @RequestParam double cost,
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> quantities,
            @RequestParam List<Double> costs,
            RedirectAttributes redirectAttributes
    ) {
        importOrderService.createImportOrder(supplierId, cost, productIds, quantities, costs);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo đơn nhập hàng thành công!");
        return "redirect:/employee/import-orders/create";

    }

}
