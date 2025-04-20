package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            Long supplierId,
            double cost,
            List<Long> productIds,
            List<Integer> quantities,
            List<Double> costs
    ) {
        // Lấy username đang đăng nhập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Tìm tài khoản
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }

        Employee employee = employeeRepository.findByEmail(account.getUsername());
        if (employee == null) {
            throw new RuntimeException("Không tìm thấy nhân viên với email = " + account.getUsername());
        }



        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

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





}
