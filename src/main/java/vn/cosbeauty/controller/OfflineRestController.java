package vn.cosbeauty.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import vn.cosbeauty.DTO.OffDetailDTO;
import vn.cosbeauty.DTO.OffOrderItemDTO;
import vn.cosbeauty.DTO.OffOrderRequestDTO;
import vn.cosbeauty.DTO.response.OffOrderItemResponseDTO;
import vn.cosbeauty.DTO.response.OffOrderResponseDTO;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.entity.OffOrderDetail;
import vn.cosbeauty.entity.OfflineOrder;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.repository.EmployeeRepository;
import vn.cosbeauty.repository.OfflineOrderRepository;
import vn.cosbeauty.repository.ProductRepository;
import vn.cosbeauty.service.ProductService;

@RestController
@RequestMapping("/api/OffOrder")
@SessionAttributes("OffOrder")
public class OfflineRestController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OfflineOrderRepository offlineOrderRepository;

    @ModelAttribute("OffOrder")
    public List<OffDetailDTO> OffOrder() {
        return new ArrayList<>();
    }

    @GetMapping("/search")
    public List<OffDetailDTO> search(@RequestParam("q") String keyword) {
        return productService.searchByName(keyword);
    }
    
    @GetMapping("/searchCustomers")
    public List<Map<String, Object>> searchCustomers(@RequestParam String q) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(q);
        return customers.stream()
        	    .map(c -> {
        	        Map<String, Object> map = new HashMap<>();
        	        map.put("id", c.getCustomerID());
        	        map.put("name", c.getName());
        	        map.put("phone", c.getPhone());
        	        return map;
        	    })
        	    .collect(Collectors.toList());
    }

    @PostMapping("/addItems")
    public List<OffDetailDTO> addItem(@RequestBody OffDetailDTO item,
                                    @ModelAttribute("OffOrder") List<OffDetailDTO> OffOrder) {
    	OffOrder.add(item);
        return OffOrder;
    }

    @DeleteMapping("/items/{id}")
    public List<OffDetailDTO> removeItem(@PathVariable Long id,
                                       @ModelAttribute("OffOrder") List<OffDetailDTO> OffOrder) {
    	OffOrder.removeIf(p -> p.getId().equals(id));
        return OffOrder;
    }

    @GetMapping("/items")
    public List<OffDetailDTO> getItems(@ModelAttribute("OffOrder") List<OffDetailDTO> OffOrder) {
        return OffOrder;
    }
    
    @PostMapping("/createOrder")
    public ResponseEntity<OffOrderResponseDTO> createOrder(@RequestBody OffOrderRequestDTO orderRequest) {
        // Tìm Customer
    	Customer customer = null;
        if (orderRequest.getCustomerId() != null) {
            customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        // Tìm Employee
        Employee employee = employeeRepository.findById(orderRequest.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Tạo OfflineOrder
        OfflineOrder order = new OfflineOrder();
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setPhone(orderRequest.getPhone());
        order.setCost(orderRequest.getCost());
        order.setDiscount(orderRequest.getDiscount());
        order.setPaymentType(orderRequest.getPaymentType());
        order.setNote(orderRequest.getNote());
        order.setOrderDate(LocalDateTime.now());
        
        // Tạo danh sách OffOrderDetail
        List<OffOrderDetail> details = new ArrayList<>();
        for (OffOrderItemDTO item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            int quantity = item.getQuantity();
            int currentStock = product.getQuantity();
            if (currentStock < quantity) {
                throw new RuntimeException("Sản phẩm " + product.getProductName() + " không đủ hàng.");
            }
            product.setQuantity(currentStock - quantity);
            
            productRepository.save(product);
            OffOrderDetail detail = new OffOrderDetail();
            detail.setOfflineOrder(order);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setCost(item.getCost());
            details.add(detail);
        }
        order.setOfflineDetails(details);
        
        // Lưu đơn hàng
        OfflineOrder savedOrder = offlineOrderRepository.save(order);
        
     // Bắt đầu mapping sang DTO
        OffOrderResponseDTO resp = new OffOrderResponseDTO();
        resp.setOffOrderID(savedOrder.getOffOrderID());
        resp.setCost(savedOrder.getCost());
        resp.setPhone(savedOrder.getPhone());
        resp.setPaymentType(savedOrder.getPaymentType());
        resp.setOrderDate(savedOrder.getOrderDate());
        resp.setCustomerId(
            savedOrder.getCustomer() != null ? savedOrder.getCustomer().getCustomerID() : null);
        resp.setEmployeeId(savedOrder.getEmployee().getEmployeeID());

        // Map từng detail
        List<OffOrderItemResponseDTO> itemDTOs = savedOrder.getOfflineDetails().stream().map(d -> {
            OffOrderItemResponseDTO di = new OffOrderItemResponseDTO();
            di.setId(d.getID());
            di.setProductId(d.getProduct().getProductID());
            di.setQuantity(d.getQuantity());
            di.setCost(d.getCost());
            return di;
        }).toList();

        resp.setItems(itemDTOs);
        
        return ResponseEntity.ok(resp);
    }
    
}
