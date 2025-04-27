package vn.cosbeauty.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.repository.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OnlineService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OnlineRepository onlineRepository;
    @Autowired
    private OnOrderDetailRepository onOrderDetail;
    @Autowired
    private CommentRepository commentRepository;


    private void restockProducts(OnlineOrder order) {
        List<OnOrderDetail> details = onOrderDetail.findOnOrderDetailByOnlineOrder_OnOrderID(order.getOnOrderID());
        for (OnOrderDetail detail : details) {
            Product product = detail.getProduct();
            int oldQty = product.getQuantity();
            product.setQuantity(oldQty + detail.getQuantity());
            productRepository.save(product);
        }
    }
    public List<OnlineOrder> getAllOnlineOrder() {
        return onlineRepository.getAllBy();
    }
    public OnlineOrder getOrderById(Long id) {
        return onlineRepository.findById(id).orElse(null);
    }
    public List<OnlineOrder> searchOrders(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllOnlineOrder();
        }
        return onlineRepository.findByNameContainingIgnoreCaseOrPhoneContaining(keyword);
    }

    public List<OnlineOrder> filterOrders(String status) {
        if (status == null || status.isEmpty()) {
            return getAllOnlineOrder();
        }
        if (status.startsWith("confirm-")) {
            int confirmStatus = Integer.parseInt(status.split("-")[1]);
            return onlineRepository.findByConfirm(confirmStatus);
        } else if (status.startsWith("delivery-")) {
            int deliveryStatus = Integer.parseInt(status.split("-")[1]);
            return onlineRepository.findByDeliveryStatus(deliveryStatus);
        }
        return getAllOnlineOrder();
    }
    public void confirmOrder(Long id) {
        OnlineOrder order = getOrderById(id);
        order.setConfirm(2); // Đã xác nhận
        onlineRepository.save(order);
    }

    public void cancelOrder(Long id, String cancelReason) {
        OnlineOrder order = getOrderById(id);
        order.setConfirm(3); // Đã hủy
        order.setCancelReason(cancelReason);
        order.setDeliveryDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        restockProducts(order);
        onlineRepository.save(order);
    }

    public void updateDeliveryStatus(Long id, int newDeliveryStatus) {
        OnlineOrder order = getOrderById(id);
        order.setDeliveryStatus(newDeliveryStatus);
        if (newDeliveryStatus == 4 || newDeliveryStatus ==5) {
            order.setDeliveryDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        }
        if (newDeliveryStatus == 5) {
            restockProducts(order);
        }

        onlineRepository.save(order);
    }
    public List<OnOrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return onOrderDetail.findOnOrderDetailByOnlineOrder_OnOrderID(orderId);
    }

    public List<OnlineOrder> getOrderStatusHistory(Long orderId) {
        return onlineRepository.findByOnOrderIDOrderByDeliveryDateDesc(orderId);
    }
//    public List<OnlineOrder> getOrdersByCustomerIdAndStatus(Customer customer, Integer confirm){
//        return onlineRepository.findOnlineOrderByCustomerAndConfirm(customer, confirm);
//    };
//    public List<OnlineOrder> getOrdersByCustomerIdAndDeliStatus(Customer customer, Integer delivery){
//        return onlineRepository.findOnlineOrderByCustomerAndDeliveryStatus(customer, delivery);
//    };
//    public List<OnlineOrder> getAllOrdersByCustomerId(Customer customer){
//      return onlineRepository.findOnlineOrderByCustomerOrderByOrderDateDesc(customer);
//    };
    public List<OnlineOrder> getOrdersByCustomerIdAndStatus(Customer customer, Integer confirm) {
        List<OnlineOrder> orders = onlineRepository.findOnlineOrderByCustomerAndConfirm(customer, confirm);
        // Sắp xếp theo orderDate giảm dần nếu không có sắp xếp sẵn trong repository
        orders.sort((order1, order2) -> order2.getOrderDate().compareTo(order1.getOrderDate()));
        return orders;
    }

    public List<OnlineOrder> getOrdersByCustomerIdAndDeliStatus(Customer customer, Integer delivery) {
        List<OnlineOrder> orders = onlineRepository.findOnlineOrderByCustomerAndDeliveryStatus(customer, delivery);
        // Sắp xếp theo orderDate giảm dần
        orders.sort((order1, order2) -> order2.getOrderDate().compareTo(order1.getOrderDate()));
        return orders;
    }


    public List<OnlineOrder> getAllOrdersByCustomerId(Customer customer){
        // Đảm bảo gọi đúng phương thức với orderDateDesc
        return onlineRepository.findOnlineOrderByCustomerOrderByOrderDateDesc(customer);
    }

    @Transactional
    public OnlineOrder createOrder(Long customerID,String customerName,String address,String phone, String email,  List<CartItem> cartItem,
                             BigDecimal subtotal, BigDecimal shippingFee, BigDecimal grandTotal) {
        int delivery = 1;// Dang xu ly =2: dang van chuyen, =3: thanh cong, =4: that bai
        int confirm = 1; //Cho xac nhan ; 2 : da xac nhan
        int paymentType = 1; // Ship cod

        Customer customer = customerRepository.findByCustomerID(customerID);
        OnlineOrder order = new OnlineOrder(customer,customerName, address, phone, grandTotal, delivery,confirm,paymentType, shippingFee,subtotal);
        onlineRepository.save(order);
        for (CartItem c: cartItem
             ) {
            Long productID = c.getProductID();
            int quantities = c.getQuantity();
            Product product = productRepository.findProductByProductID(productID);
            // tru quantities trong product
            int currentStock = product.getQuantity();
            if (currentStock < quantities) {
                throw new RuntimeException("Sản phẩm " + product.getProductName() + " không đủ hàng.");
            }
            product.setQuantity(currentStock - quantities);
            productRepository.save(product);


            BigDecimal cost = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantities));
            OnOrderDetail orderItem = new OnOrderDetail(product,cost, quantities);
            orderItem.setOnlineOrder(order);
            onOrderDetail.save(orderItem);
        }
        return order;
    }
    public boolean hasCustomerReviewedOrder(Long orderId, Long customerId) {
        List<OnOrderDetail> orderDetails = getOrderDetailsByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            return false; // No products to review
        }
        List<Long> productIds = orderDetails.stream()
                .map(detail -> detail.getProduct().getProductID())
                .collect(Collectors.toList());
        List<Comment> reviews = commentRepository.findByCustomer_CustomerIDAndProduct_ProductIDIn(customerId, productIds);
        return !reviews.isEmpty(); // True if any reviews exist
    }
    private static final Logger logger = LoggerFactory.getLogger(OnlineService.class);
    public Map<String, Long> getOrderStatusCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("pending", (long) filterOrders("confirm-1").size());
        counts.put("confirmed", (long) filterOrders("confirm-2").size());
        counts.put("canceled", (long) filterOrders("confirm-3").size());
        counts.put("processing", (long) filterOrders("delivery-2").size());
        counts.put("delivering", (long) filterOrders("delivery-3").size());
        counts.put("success", (long) filterOrders("delivery-4").size());
        counts.put("failed", (long) filterOrders("delivery-5").size());
        logger.info("Order status counts: {}", counts);
        return counts;
    }

}
