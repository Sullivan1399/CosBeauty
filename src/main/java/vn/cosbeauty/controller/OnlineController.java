package vn.cosbeauty.controller;

import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.DTO.ShipAddressDTO;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class OnlineController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OnlineService onlineService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/checkout"})
    public String checkout(Model model) {
        Long customerId = customerService.getCurrentCustomerID();
        Customer customer = customerService.findCustomerByID(customerId);
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(customerId);
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice()).multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng giá trị
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        model.addAttribute("shippingAddress", new ShipAddressDTO());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("customer", customer);
        model.addAttribute("totalAmount", totalAmount);
        return "web/checkout";
    }
    @PostMapping("/order")
    public String processOrder(
            @RequestParam String customerName,
            @RequestParam String customerAddress,
            @RequestParam String customerPhone,
            @RequestParam String customerEmail,
            @RequestParam BigDecimal subtotal,
            @RequestParam BigDecimal shippingFee,
            @RequestParam BigDecimal grandTotal
    ) {
        Long customerId = customerService.getCurrentCustomerID();
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(customerId);
        OnlineOrder onlineOrder = onlineService.createOrder(customerId, customerName, customerAddress, customerPhone,  customerEmail, cartItems,
                 subtotal,  shippingFee,  grandTotal);
        cartService.clearCartByCustomerId(customerId);
        return "redirect:/orders";

    }
    @GetMapping("/orders")
    public String viewOrders(Model model) {
//        int delivery = 1;// Dang xu ly =2: dang van chuyen, =3: thanh cong, =4: that bai=5
//        int confirm = 1; //Cho xac nhan ; 2 : da xac nhan; 3 : da huy
//        int paymentType = 1; // Ship cod
        Long customerId = customerService.getCurrentCustomerID();
        Customer customer = customerService.findCustomerByID(customerId);
        List<OnlineOrder> allOrders = onlineService.getAllOrdersByCustomerId(customer);
        List<OnlineOrder> newOrders = onlineService.getOrdersByCustomerIdAndStatus(customer, 1);
        List<OnlineOrder> confirmOrders = onlineService.getOrdersByCustomerIdAndStatus(customer, 2);
        List<OnlineOrder> cancelOrders = onlineService.getOrdersByCustomerIdAndStatus(customer, 3);
        List<OnlineOrder> processingOrders = onlineService.getOrdersByCustomerIdAndDeliStatus(customer, 2);
        List<OnlineOrder> deliveringOrders = onlineService.getOrdersByCustomerIdAndDeliStatus(customer, 3);
        List<OnlineOrder> successOrders = onlineService.getOrdersByCustomerIdAndDeliStatus(customer,4 );
        List<OnlineOrder> lossOrders = onlineService.getOrdersByCustomerIdAndDeliStatus(customer, 5);

        model.addAttribute("allOrders", allOrders);
        model.addAttribute("newOrders", newOrders);
        model.addAttribute("confirmOrders", confirmOrders);
        model.addAttribute("cancelledOrders", cancelOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("deliveringOrders", deliveringOrders);
        model.addAttribute("successOrders", successOrders);
        model.addAttribute("lossOrders", lossOrders);

        return "web/cus_ha1"; // Thymeleaf template name
    }
    @GetMapping("/order/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        OnlineOrder order = onlineService.getOrderById(id);
        if (order == null) {
            return "redirect:/orders"; // Chuyển hướng nếu không tìm thấy đơn hàng
        }
        List<OnOrderDetail> orderDetails = onlineService.getOrderDetailsByOrderId(id);
        List<OnlineOrder> statusHistory = onlineService.getOrderStatusHistory(id);
        Long cusID = customerService.getCurrentCustomerID();
        boolean hasReviewed = onlineService.hasCustomerReviewedOrder(id, cusID);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("statusHistory", statusHistory);
        model.addAttribute("hasReviewed", hasReviewed);
        return "web/order-detail";
    }
//    @GetMapping("/admin/manage-admin")
//    public String getAllOrders(Model model,
//                               @RequestParam(required = false) String search,
//                               @RequestParam(required = false) String status) {
//        List<OnlineOrder> orders;
//        if (search != null && !search.trim().isEmpty()) {
//            orders = onlineService.searchOrders(search);
//        } else if (status != null && !status.isEmpty()) {
//            orders = onlineService.filterOrders(status);
//        } else {
//            orders = onlineService.getAllOnlineOrder();
//        }
//        model.addAttribute("orders", orders);
//        model.addAttribute("search", search);
//        model.addAttribute("status", status);
//        return "web/Manage-admin";
//    }

    @GetMapping("/admin/admin-detail/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        OnlineOrder order = onlineService.getOrderById(id);
        List<OnOrderDetail> orderDetails = onlineService.getOrderDetailsByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "web/admin-detail";
    }

    @PostMapping("/admin/confirm/{id}")
    public String confirmOrder(@PathVariable Long id) {
        onlineService.confirmOrder(id);
        return "redirect:/admin/manage-admin";
    }

    @PostMapping("/admin/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, @RequestParam("cancelReason") String cancelReason) {
        onlineService.cancelOrder(id, cancelReason);
        return "redirect:/admin/manage-admin";
    }
    @PostMapping("/admin/update-delivery/{id}")
    public String updateDeliveryStatus(@PathVariable Long id, @RequestParam int deliveryStatus, RedirectAttributes redirectAttributes) {
        try {
            onlineService.updateDeliveryStatus(id, deliveryStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái giao hàng thành công!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/manage-admin";
    }
    @GetMapping("/admin/manage-admin")
    public String manageAdmin(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            Model model) {
        List<OnlineOrder> orders;

        // Nếu không có keyword và status, lấy tất cả đơn hàng
        if ((keyword == null || keyword.trim().isEmpty()) && (status == null || status.isEmpty())) {
            orders = onlineService.getAllOnlineOrder();
        } else {
            // Lấy danh sách theo trạng thái hoặc tất cả nếu status rỗng
            orders = status != null && !status.isEmpty() ? onlineService.filterOrders(status) : onlineService.getAllOnlineOrder();

            // Áp dụng tìm kiếm nếu có keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                List<OnlineOrder> searchedOrders = onlineService.searchOrders(keyword);
                orders.retainAll(searchedOrders); // Giữ đơn hàng thỏa mãn cả lọc và tìm kiếm
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("hasResults", !orders.isEmpty());

        return "web/Manage-admin";
    }
    @GetMapping("/reorder/{id}")
    public String reorder(@PathVariable("id") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            // Get the current customer ID
            Long customerId = customerService.getCurrentCustomerID();

            // Fetch the order
            OnlineOrder order = onlineService.getOrderById(orderId);
            if (order == null) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại.");
                return "redirect:/orders";
            }

            // Verify the order belongs to the customer
            if (!order.getCustomer().getCustomerID().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập đơn hàng này.");
                return "redirect:/orders";
            }

            // Add order items to the cart
            List<OnOrderDetail> orderDetails = onlineService.getOrderDetailsByOrderId(orderId);
            for (OnOrderDetail detail : orderDetails) {
                Product product = detail.getProduct();
                int quantity = detail.getQuantity();

                // Check stock availability
                if (product.getQuantity() < quantity) {
                    redirectAttributes.addFlashAttribute("error",
                            "Sản phẩm " + product.getProductName() + " không đủ hàng.");
                    return "redirect:/orders";
                }

                // Add to cart
                cartService.addReOrderToCart(customerId, product.getProductID(), quantity);
            }

            redirectAttributes.addFlashAttribute("success", "Đã thêm các sản phẩm vào giỏ hàng!");
            return "redirect:/api/cart/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
            return "redirect:/orders";
        }
    }
    @PostMapping("/review")
    public String submitReview(
            @RequestParam("orderId") Long orderId,
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        try {
            Long customerId = customerService.getCurrentCustomerID();
            OnlineOrder order = onlineService.getOrderById(orderId);
            if (order == null || !order.getCustomer().getCustomerID().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng không hợp lệ.");
                return "redirect:/orders";
            }
            for (Long productId : productIds) {
                String ratingKey = "ratings_" + productId;
                String commentKey = "comments_" + productId;
                int rating = Integer.parseInt(allParams.get(ratingKey));
                String commentText = allParams.get(commentKey);
                if (rating < 1 || rating > 5) {
                    redirectAttributes.addFlashAttribute("error", "Điểm đánh giá phải từ 1 đến 5.");
                    return "redirect:/order/" + orderId;
                }
                commentService.saveReview(customerId, productId, commentText, rating);
            }
            redirectAttributes.addFlashAttribute("success", "Đánh giá của bạn đã được gửi!");
            return "redirect:/order/" + orderId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi đánh giá: " + e.getMessage());
            return "redirect:/order/" + orderId;
        }
    }

}
