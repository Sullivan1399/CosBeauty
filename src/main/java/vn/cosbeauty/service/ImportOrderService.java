package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ImportOrderService {

    @Autowired
    private ImportOrderRepository importOrderRepository;

    @Autowired
    private ImportOrderDetailRepository importOrderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Tạo đơn nhập hàng mới, lưu vào cả bảng importOrder và importDetail
     */
    public void createImportOrder(Employee employee, Long supplierId, BigDecimal cost, List<Long> productIds,
                                  List<Integer> quantities) {
        if (employee == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống");
        }
        if (supplierId == null) {
            throw new IllegalArgumentException("ID nhà cung cấp không được để trống");
        }
        // Safely convert Long to int
        if (supplierId > Integer.MAX_VALUE || supplierId < Integer.MIN_VALUE) {
            throw new IllegalArgumentException("ID nhà cung cấp vượt quá giới hạn cho phép");
        }
        int supId = supplierId.intValue();

        Supplier supplier = supplierRepository.findById(supId)
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại"));

        ImportOrder order = new ImportOrder();
        order.setEmployee(employee);
        order.setSupplier(supplier);
        order.setCost(cost);
        order.setImportDate(LocalDateTime.now());
        order.setStatus(0); // Set status to 0

        List<ImportOrderDetail> details = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
            ImportOrderDetail detail = new ImportOrderDetail();
            detail.setProduct(product);
            detail.setQuantity(quantities.get(i));
            detail.setCost(product.getPrice()); // Use Product.price
            detail.setImportOrder(order);
            details.add(detail);
        }
        order.setImportDetail(details);
        importOrderRepository.save(order);
    }
    /**
     * Lấy tất cả đơn nhập hàng
     */
    public List<ImportOrder> getAllImportOrders() {
        return importOrderRepository.findAll();
    }

    /**
     * Lấy đơn nhập hàng theo ID
     */
    public ImportOrder getOrderById(Long importID) {
        return importOrderRepository.findById(importID).orElse(null);
    }

    /**
     * Lưu hoặc cập nhật đơn nhập hàng
     */
    public void saveOrder(ImportOrder order) {
        importOrderRepository.save(order);
    }

    /**
     * Lấy danh sách đơn nhập hàng với phân trang và bộ lọc
     */
    public Page<ImportOrder> getFilteredImportOrders(LocalDateTime importDate, Integer status, Pageable pageable) {
        if (importDate != null && status != null) {
            return importOrderRepository.findByImportDateAndStatus(importDate, status, pageable);
        } else if (importDate != null) {
            return importOrderRepository.findByImportDate(importDate, pageable);
        } else if (status != null) {
            return importOrderRepository.findByStatus(status, pageable);
        } else {
            return importOrderRepository.findAll(pageable);
        }
    }

    /**
     * Các phương thức phân trang cụ thể
     */
    public Page<ImportOrder> getAllImportOrders(Pageable pageable) {
        return importOrderRepository.findAll(pageable);
    }

    public Page<ImportOrder> findByImportDate(LocalDateTime importDate, Pageable pageable) {
        return importOrderRepository.findByImportDate(importDate, pageable);
    }

    public Page<ImportOrder> findByStatus(int status, Pageable pageable) {
        return importOrderRepository.findByStatus(status, pageable);
    }

    public Page<ImportOrder> findByImportDateAndStatus(LocalDateTime importDate, int status, Pageable pageable) {
        return importOrderRepository.findByImportDateAndStatus(importDate, status, pageable);
    }
}