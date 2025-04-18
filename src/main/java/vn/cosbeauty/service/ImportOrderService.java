package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.repository.*;

import java.math.BigDecimal;
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

    /**
     * Tạo đơn nhập hàng mới, lưu vào cả bảng importOrder và importDetail
     */
    @Transactional
    public void createImportOrder(Long supplierId, double totalCost,
                                  List<Long> productIds, List<Integer> quantities, List<Double> costs) {

        if (productIds.size() != quantities.size() || productIds.size() != costs.size()) {
            throw new IllegalArgumentException("Dữ liệu sản phẩm không hợp lệ");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        // Lấy nhân viên tạm thời (mặc định employeeID = 1)
        Employee employee = employeeRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        // Tạo đơn nhập
        ImportOrder order = new ImportOrder(
                employee,
                BigDecimal.valueOf(totalCost),
                1, // status mặc định = 1 (có thể là "đã đặt hàng")
                supplier
        );

        // Lưu đơn nhập
        ImportOrder savedOrder = importOrderRepository.save(order);

        // Lưu từng chi tiết đơn nhập
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            ImportOrderDetail detail = new ImportOrderDetail(
                    product,
                    BigDecimal.valueOf(costs.get(i)),
                    quantities.get(i)
            );
            detail.setImportOrder(savedOrder);
            importOrderDetailRepository.save(detail);
        }
    }
}
