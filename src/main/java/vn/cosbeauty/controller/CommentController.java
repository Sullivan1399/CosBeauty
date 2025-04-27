package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.repository.OnlineRepository;
import vn.cosbeauty.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CommentController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OnlineService onlineService;
    @Autowired
    private OnlineRepository onlineRepository;

    // Hiển thị trang đánh giá
    @GetMapping("/web/comment")
    public String showCommentPage(@RequestParam("OnOrderID") Long orderId, Model model) {
        // Lấy ID khách hàng hiện tại
        Long customerId = customerService.getCurrentCustomerID();
        if (customerId == null) {
            return "redirect:/web/login";
        }

        // Lấy thông tin khách hàng
        Customer customer = customerService.findCustomerByID(customerId);
        if (customer == null) {
            model.addAttribute("error", "Không tìm thấy thông tin khách hàng.");
            return "error";
        }

        // Lấy đơn hàng theo orderId
        OnlineOrder order = onlineRepository.findByOnOrderID(orderId);
        if (order == null || !order.getCustomer().getCustomerID().equals(customerId)) {
            model.addAttribute("error", "Đơn hàng không tồn tại hoặc không thuộc về bạn.");
            return "error";
        }

        // Lấy danh sách sản phẩm trong đơn hàng
        List<Product> purchasedProducts = new ArrayList<>();
        List<Long> commentCounts = new ArrayList<>();
        List<OnOrderDetail> orderDetails = onlineService.getOrderDetailsByOrderId(orderId);
        for (OnOrderDetail detail : orderDetails) {
            Product product = detail.getProduct();
            if (product != null) {
                purchasedProducts.add(product);
                long commentCount = commentService.countByCustomerAndProductAndOnlineOrder(customer, product, order);
                commentCounts.add(commentCount);
            }
        }

        // Kiểm tra nếu không có sản phẩm
        if (purchasedProducts.isEmpty()) {
            model.addAttribute("error", "Không có sản phẩm nào trong đơn hàng này để đánh giá.");
            return "error";
        }

        // Truyền dữ liệu vào model
        model.addAttribute("purchasedProducts", purchasedProducts);
        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("customerId", customerId);
        model.addAttribute("orderId", orderId);

        return "web/comment";
    }

    // Xử lý khi nhấn "Hoàn Thành" - Lưu tất cả đánh giá
    @PostMapping("/web/completeReviews")
    public String submitReview(
            @RequestParam("orderId") Long orderId,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin khách hàng (customerId)
            Long customerId = customerService.getCurrentCustomerID();
            OnlineOrder order = onlineService.getOrderById(orderId);

            // Kiểm tra tính hợp lệ của đơn hàng
            if (order == null || !order.getCustomer().getCustomerID().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng không hợp lệ hoặc không phải của bạn.");
                return "redirect:/orders";
            }

            // Kiểm tra có ít nhất một sản phẩm được chọn để đánh giá
            List<Long> productIds = new ArrayList<>();
            allParams.forEach((key, value) -> {
                if (key.startsWith("rate[")) {
                    productIds.add(Long.parseLong(key.substring(5, key.length() - 1)));
                }
            });

            if (productIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất một sản phẩm để đánh giá.");
                return "redirect:/order/" + orderId;
            }

            // Lưu các đánh giá và bình luận của từng sản phẩm
            Map<Long, Integer> ratings = new HashMap<>();
            Map<Long, String> comments = new HashMap<>();

            for (Long productId : productIds) {
                // Lấy rating và comment từ các thông tin form
                String ratingStr = allParams.get("rate[" + productId + "]");
                String commentText = allParams.get("comment[" + productId + "]");

                // Kiểm tra tính hợp lệ của rating
                int rating;
                try {
                    rating = Integer.parseInt(ratingStr);
                    if (rating < 1 || rating > 5) {
                        redirectAttributes.addFlashAttribute("error", "Điểm đánh giá phải từ 1 đến 5 cho sản phẩm ID: " + productId);
                        return "redirect:/order/" + orderId;
                    }
                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("error", "Điểm đánh giá không hợp lệ cho sản phẩm ID: " + productId);
                    return "redirect:/order/" + orderId;
                }

                // Lưu lại đánh giá và bình luận
                ratings.put(productId, rating);
                comments.put(productId, commentText != null ? commentText : "");
            }

            // Lưu các đánh giá vào cơ sở dữ liệu
            commentService.saveReview(customerId, orderId, productIds, ratings, comments);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success", "Đánh giá của bạn đã được gửi thành công!");
            return "redirect:/order/" + orderId;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order/" + orderId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi đánh giá: " + e.getMessage());
            return "redirect:/order/" + orderId;
        }
    }



//    @PostMapping("/web/postComment")
//    public String submitReview(
//            @RequestParam(value = "comment", required = false) String commentText,
//            @RequestParam(value = "rate", required = false) String rateStr,
//            @RequestParam(value = "productID") String productIDStr,  // Lấy giá trị productID từ form
//            RedirectAttributes redirectAttributes) {
//        System.out.println("Received form data: comment=" + commentText + ", rateStr=" + rateStr + ", productID=" + productIDStr);
//
//        Long productID = null;
//        productID = Long.parseLong(productIDStr);
//
//        // Xử lý đánh giá và lưu bình luận vào database
//        int rating = 0;
//            if (rateStr != null && !rateStr.trim().isEmpty()) {
//            // Thay thế dấu phẩy thành dấu chấm nếu cần thiết
//            rateStr = rateStr.replace(',', '.');
//
//            // Lấy phần trước dấu phẩy (hoặc dấu chấm) và chuyển thành Integer
//            String[] parts = rateStr.split("\\.");  // Tách tại dấu chấm (hoặc dấu phẩy đã thay thế)
//            String integerPart = parts[0];  // Lấy phần trước dấu chấm/phẩy
//                rating = Integer.parseInt(integerPart);
//            }
//        System.out.println(rating);
//        // Kiểm tra comment
//        if (commentText == null || commentText.trim().isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập nội dung bình luận.");
//            return "redirect:/web/product/" + productID;
//        }
//
//        // Lấy customerId
//        Long customerId = customerService.getCurrentCustomerID();
//        if (customerId == null) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để gửi đánh giá.");
//            return "redirect:/web/product/" + productID;
//        }
//        Customer customer = customerService.findCustomerByID(customerId);
//        Product product = productService.getProductById(productID);
//        long purchaseCount = onlineRepository.countByCustomerAndOnOrderDetails_Product(customer, product);
//
//        if (purchaseCount == 0) {
//            redirectAttributes.addFlashAttribute("error", "Bạn cần phải mua sản phẩm trước khi đánh giá.");
//            return "redirect:/web/product/" + productID;
//        }
//        try {
//            commentService.saveReview(customerId, productID, commentText, rating);
//            redirectAttributes.addFlashAttribute("message", "Đánh giá của bạn đã được gửi thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("message", "Lỗi khi gửi đánh giá: " + e.getMessage());
//        }
//        return "redirect:/web/product/" + productID;
//    }


}
