package vn.cosbeauty.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.cosbeauty.entity.OffOrderDetail;
import vn.cosbeauty.entity.OfflineOrder;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.repository.EmployeeRepository;
import vn.cosbeauty.repository.OffOrderDetailRepository;
import vn.cosbeauty.repository.OfflineOrderRepository;
import vn.cosbeauty.repository.ProductRepository;

@Service
public class OfflineOrderService {
	
	@Autowired
	private OfflineOrderRepository offlineOrderRepository;
	@Autowired
    private OffOrderDetailRepository offOrderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    
	public void addOfflineOrder(OfflineOrder offlineOrder) {
		offlineOrderRepository.save(offlineOrder);
	}
	
    public List<OfflineOrder> getAllOffOrder() {
    	return offlineOrderRepository.findAll();
    }
    
    public OfflineOrder getOrderById(Long id) {
    	return offlineOrderRepository.findById(id).orElse(null);
    }
    
    public List<OfflineOrder> searchOrders(String keyword) {
    	return offlineOrderRepository.findByNameContainingIgnoreCaseOrPhoneContaining(keyword);
    }

	public List<OffOrderDetail> getOffOrderDetail(Long id) {
		return offOrderDetailRepository.getOffOrderDetailByOffOrderId(id);
	}
    
//    public OfflineOrder getOffOrderById(Long id) {
//    	return offlineOrderRepository.getById(id).orElse(null);
//    }
//    public OfflineOrder createOrder(OfflineOrderRequest request) {
//        OfflineOrder order = new OfflineOrder();
//        order.setPhone(request.getPhone());
//        order.setDiscount(request.getDiscount());
//        order.setPaymentType(request.getPaymentType());
//        order.setNote(request.getNote());
//        order.setOrderDate(LocalDateTime.now());
//
//        // Giả sử lấy employee hiện tại từ session hoặc logic khác
//        Employee employee = employeeRepository.findById(1L).orElse(null); // Thay bằng logic thực tế
//        order.setEmployee(employee);
//
//        // Tìm hoặc tạo customer dựa trên số điện thoại
//        Customer customer = customerRepository.findByPhone(request.getPhone()).orElse(null);
//        order.setCustomer(customer);
//
//        BigDecimal totalCost = BigDecimal.ZERO;
//        List<OffOrderDetail> details = new ArrayList<>();
//
//        for (OffOrderDetailDTO detailDTO : request.getDetails()) {
//            Product product = productRepository.findById(detailDTO.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//            BigDecimal cost = product.getPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity()));
//            OffOrderDetail detail = new OffOrderDetail(product, cost, detailDTO.getQuantity());
//            detail.setOfflineOrder(order);
//            details.add(detail);
//            totalCost = totalCost.add(cost);
//        }
//
//        order.setCost(totalCost);
//        order.setOfflineDetails(details);
//
//        return offlineOrderRepository.save(order);
//    }
}
