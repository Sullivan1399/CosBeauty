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
    public void createImportOrder(
            int supplierId,
            double cost,
            List<Long> productIds,
            List<Integer> quantities,
            List<Double> costs) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }

        Employee employee = employeeRepository.findByEmail(account.getUsername());
        if (employee == null) {
            throw new RuntimeException("Không tìm thấy nhân viên với email = " + account.getUsername());
        }

        Supplier supplier = supplierRepository.findById(supplierId);

        ImportOrder order = new ImportOrder();
        order.setEmployee(employee);
        order.setImportDate(LocalDateTime.now());
        order.setSupplier(supplier);

        ImportOrder savedOrder = importOrderRepository.save(order);

        BigDecimal totalCost = BigDecimal.ZERO;

        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            BigDecimal itemCost = BigDecimal.valueOf(costs.get(i));
            int quantity = quantities.get(i);

            totalCost = totalCost.add(itemCost.multiply(BigDecimal.valueOf(quantity)));

            ImportOrderDetail detail = new ImportOrderDetail(product, itemCost, quantity);
            detail.setImportOrder(savedOrder);
            importOrderDetailRepository.save(detail);
        }

        savedOrder.setCost(totalCost);
        importOrderRepository.save(savedOrder);
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
    public Page<ImportOrder> getFilteredImportOrders(LocalDate importDate, Integer status, Pageable pageable) {
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

    public Page<ImportOrder> findByImportDate(LocalDate importDate, Pageable pageable) {
        return importOrderRepository.findByImportDate(importDate, pageable);
    }

    public Page<ImportOrder> findByStatus(int status, Pageable pageable) {
        return importOrderRepository.findByStatus(status, pageable);
    }

    public Page<ImportOrder> findByImportDateAndStatus(LocalDate importDate, int status, Pageable pageable) {
        return importOrderRepository.findByImportDateAndStatus(importDate, status, pageable);
    }
}