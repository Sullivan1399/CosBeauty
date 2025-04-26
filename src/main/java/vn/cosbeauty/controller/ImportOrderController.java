package vn.cosbeauty.controller;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.entity.ImportOrder;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.repository.*;
import vn.cosbeauty.service.ImportOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ImportOrderController {

    @Autowired
    private ImportOrderService importOrderService;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/employee/import-orders/create")
    public String showCreateForm(Model model) {
        List<Supplier> suppliers = supplierRepository.findAll();
        if (suppliers.isEmpty()) {
            model.addAttribute("errorMessage", "Không có nhà cung cấp nào trong hệ thống!");
        }
        model.addAttribute("suppliers", suppliers);
        return "web/create-import-order";
    }

    @PostMapping("/employee/import-orders/create")
    public String saveImportOrder(
            @RequestParam Long supplierId,
            @RequestParam String cost,
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> quantities,
            RedirectAttributes redirectAttributes
    ) {
        if (productIds.isEmpty() || quantities.isEmpty() || productIds.size() != quantities.size()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu sản phẩm không hợp lệ!");
            return "redirect:/employee/import-orders";
        }
        try {
            // Parse cost to BigDecimal
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            BigDecimal parsedCost = new BigDecimal(numberFormat.parse(cost).toString());

            // Get current employee using email
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee employee = employeeRepository.findByEmail(email);
            if (employee == null) {
                throw new IllegalArgumentException("Nhân viên không tồn tại");
            }

            importOrderService.createImportOrder(employee, supplierId, parsedCost, productIds, quantities);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo đơn nhập hàng thành công!");
        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi định dạng chi phí!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employee/import-orders";
    }

    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getProductsBySupplier(@RequestParam Long supplierId) {
        return productRepository.findBySupplierSupID(supplierId);
    }

    @GetMapping("/employee/import-orders")
    public String listImportOrders(
            Model model,
            @RequestParam(value = "importDate", required = false) String importDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Chuyển đổi importDate từ String sang LocalDateTime nếu có
        LocalDateTime searchDateTime = null;
        if (importDate != null && !importDate.isEmpty()) {
            LocalDate searchDate = LocalDate.parse(importDate);
            searchDateTime = searchDate.atStartOfDay(); // Convert LocalDate to LocalDateTime (midnight)
        }

        // Chuyển đổi status từ String sang Integer nếu có
        Integer statusValue = null;
        if (status != null && !status.isEmpty()) {
            statusValue = Integer.parseInt(status);
        }

        // Lấy danh sách đơn nhập hàng với phân trang và bộ lọc
        Page<ImportOrder> importOrderPage = importOrderService.getFilteredImportOrders(searchDateTime, statusValue, pageable);

        // Định dạng dữ liệu để hiển thị
        List<Map<String, Object>> formattedImportOrders = importOrderPage.getContent().stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("importID", order.getImportID());
            map.put("cost", order.getCost());
            map.put("importDate", order.getImportDate() != null
                    ? order.getImportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "N/A");
            map.put("status", order.getStatus());
            map.put("cancelReason", order.getCancelReason() != null ? order.getCancelReason() : "Không có");
            return map;
        }).collect(Collectors.toList());

        // Thêm các thuộc tính vào model
        model.addAttribute("importOrders", formattedImportOrders);
        model.addAttribute("currentPage", importOrderPage.getNumber());
        model.addAttribute("totalPages", importOrderPage.getTotalPages());
        model.addAttribute("totalItems", importOrderPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("importDate", importDate);
        model.addAttribute("status", status);

        return "web/employee-import-orders";
    }
    @GetMapping("/employee/import-orders/view")
    public String viewOrderDetailForEmployee(@RequestParam(value = "importID", required = true) Long importID, Model model) {
        ImportOrder importOrder = importOrderService.getOrderById(importID);
        if (importOrder == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng nhập với ID: " + importID);
            return "redirect:/employee/import-orders";
        }
        model.addAttribute("importOrder", importOrder);
        return "web/employee-import-orders-view";
    }


    @GetMapping("/admin/import-orders")
    public String listImportOrdersAsAdmin(
            Model model,
            @RequestParam(value = "importDate", required = false) String importDate,
            @RequestParam(value = "status", required = false) String status,
            @PageableDefault(size = 10, sort = "importID") Pageable pageable) {

        // Lọc theo ngày nhập và trạng thái nếu có
        LocalDateTime searchDateTime = null;
        if (importDate != null && !importDate.isEmpty()) {
            LocalDate searchDate = LocalDate.parse(importDate);
            searchDateTime = searchDate.atStartOfDay(); // Convert LocalDate to LocalDateTime (midnight)
        }

        Integer statusValue = (status != null && !status.isEmpty()) ? Integer.parseInt(status) : null;

        // Lấy danh sách đơn nhập hàng với phân trang và lọc
        Page<ImportOrder> importOrdersPage = importOrderService.getFilteredImportOrders(searchDateTime, statusValue, pageable);

        // Định dạng dữ liệu để hiển thị
        List<Map<String, Object>> formattedImportOrders = importOrdersPage.getContent().stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("importID", order.getImportID());
            map.put("cost", order.getCost());
            map.put("importDate", order.getImportDate() != null
                    ? order.getImportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "N/A");
            map.put("status", order.getStatus());
            map.put("statusText", order.getStatus() == 1 ? "Đã duyệt" : order.getStatus() == 2 ? "Đã hủy" : "Chưa duyệt");
            map.put("cancelReason", order.getCancelReason() != null ? order.getCancelReason() : "Không có");
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("importOrders", formattedImportOrders);
        model.addAttribute("page", importOrdersPage);
        model.addAttribute("importDate", importDate);
        model.addAttribute("status", status);

        return "web/manage-import-orders";
    }

    @Autowired
    private ImportOrderRepository importOrderRepository;

    @GetMapping("/admin/import-orders/action")
    public String viewOrderDetail(@RequestParam(value = "importID", required = true) Long importID, Model model) {
        ImportOrder importOrder = importOrderService.getOrderById(importID);
        if (importOrder == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng nhập với ID: " + importID);
            return "redirect:/admin/import-orders";
        }
        model.addAttribute("importOrder", importOrder);
        return "web/import-order-action";
    }

    @PostMapping("/admin/import-orders/action")
    public String handleImportOrderAction(
            @RequestParam(value = "importID", required = true) Long importID,
            @RequestParam(value = "action", required = true) String action,
            @RequestParam(value = "cancelReason", required = false) String cancelReason,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Tìm đơn nhập hàng
        ImportOrder importOrder = importOrderService.getOrderById(importID);
        if (importOrder == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn nhập hàng với ID: " + importID);
            return "redirect:/admin/import-orders";
        }

        try {
            if ("approve".equalsIgnoreCase(action)) {
                // Xử lý hành động duyệt
                if (importOrder.getStatus() == 1) {
                    model.addAttribute("warning", "Đơn hàng đã được duyệt trước đó!");
                    model.addAttribute("importOrder", importOrder);
                    return "web/import-order-action";
                }
                if (importOrder.getStatus() == 2) {
                    model.addAttribute("warning", "Đơn hàng đã bị hủy, không thể duyệt!");
                    model.addAttribute("importOrder", importOrder);
                    return "web/import-order-action";
                }
                importOrder.setStatus(1);
                importOrderService.saveOrder(importOrder);
                model.addAttribute("success", "Duyệt đơn hàng thành công!");
                model.addAttribute("importOrder", importOrder);
                return "web/import-order-action";
            } else if ("cancel".equalsIgnoreCase(action)) {
                // Xử lý hành động hủy
                if (cancelReason == null || cancelReason.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Lý do hủy không được để trống");
                    return "redirect:/admin/import-orders";
                }
                if (importOrder.getStatus() == 1) {
                    redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn đã duyệt");
                    return "redirect:/admin/import-orders";
                }
                if (importOrder.getStatus() == 2) {
                    redirectAttributes.addFlashAttribute("error", "Đơn hàng đã bị hủy trước đó");
                    return "redirect:/admin/import-orders";
                }
                importOrder.setStatus(2); // Đặt trạng thái thành Đã hủy
                importOrder.setCancelReason(cancelReason);
                importOrderService.saveOrder(importOrder);
                redirectAttributes.addFlashAttribute("success", "Hủy đơn nhập hàng thành công");
                return "redirect:/admin/import-orders";
            } else {
                redirectAttributes.addFlashAttribute("error", "Hành động không hợp lệ");
                return "redirect:/admin/import-orders";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xử lý đơn nhập hàng: " + e.getMessage());
            return "redirect:/admin/import-orders";
        }
    }
}
