package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.repository.*;
import java.text.DecimalFormat;

import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OnlineRepository onlineRepository;
    public List<Comment> getAll()
    {
        return commentRepository.findAll();
    }


    // Hàm tính điểm đánh giá trung bình của sản phẩm

//    public boolean hasReviewedProductInOrder(Customer customer, Product product, OnlineOrder order) {
//        return commentRepository.existsByCustomerAndProductAndOnlineOrder(customer, product, order);
//    }
//    public boolean hasPurchasedProduct(Customer customer, Product product) {
//        return onOrderDetailRepository.countByCustomerAndProduct(customer, product) > 0;
//    }

//    public void saveReview(Long customerId, Long productId, String commentText, int rating, Long orderID) {
//
//        Customer customer = customerRepository.findByCustomerID(customerId);
//        Product product = productRepository.findProductByProductID(productId);
//
//        long commentCount = commentRepository.countByCustomerAndProduct(customer, product);
//
//        long purchaseCount = onlineRepository.countByCustomerAndOnOrderDetails_Product(customer, product);
//        System.out.println(purchaseCount);
//
//        if (commentCount >= purchaseCount) {
//            throw new IllegalArgumentException("Số lần đánh giá không thể lớn hơn hoặc bằng số lần mua sản phẩm.");
//        }
//
//        // Tạo đối tượng bình luận và lưu vào cơ sở dữ liệu
//        Comment comment = new Comment(commentText, rating, product, customer);
//        commentRepository.save(comment);
//    }
    public List<Comment> findByCustomerAndProductAndOrder(Customer customer, Product product, OnlineOrder order) {
        return commentRepository.findByCustomerAndProductAndOnlineOrder(customer, product, order);
    }
    public void saveReview(Long customerId, Long orderId, List<Long> productIds, Map<Long, Integer> ratings, Map<Long, String> comments) {
        Customer customer = customerRepository.findByCustomerID(customerId);
        OnlineOrder order = onlineRepository.findByOnOrderID(orderId);

        if (customer == null || order == null) {
            throw new IllegalArgumentException("Khách hàng hoặc đơn hàng không tồn tại.");
        }

        for (Long productId : productIds) {
            Product product = productRepository.findProductByProductID(productId);
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại: " + productId);
            }

            boolean productInOrder = order.getOnOrderDetails().stream()
                    .anyMatch(detail -> detail.getProduct().getProductID().equals(productId));
            if (!productInOrder) {
                throw new IllegalArgumentException("Sản phẩm không thuộc đơn hàng: " + productId);
            }

            long commentCount = commentRepository.countByCustomerAndProductAndOnlineOrder(customer, product, order);
            if (commentCount > 0) {
                throw new IllegalArgumentException("Bạn đã đánh giá sản phẩm này trong đơn hàng này: " + product.getProductName());
            }

            Integer rating = ratings.get(productId);
            String commentText = comments.get(productId);
            if (rating == null || rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Điểm đánh giá không hợp lệ cho sản phẩm: " + product.getProductName());
            }
            if (commentText == null) {
                commentText = "";
            }

            Comment comment = new Comment(commentText, rating, product, customer, order);
            commentRepository.save(comment);
        }
    }
//    public void saveReview(Long customerId, Long productId, String commentText, int rating, Long orderId) {
//        Customer customer = customerRepository.findByCustomerID(customerId);
//        Product product = productRepository.findProductByProductID(productId);
//        OnlineOrder order = onlineRepository.findByOnOrderID(orderId);
//
//        // Kiểm tra null
//        if (customer == null || product == null || order == null) {
//            throw new IllegalArgumentException("Khách hàng, sản phẩm hoặc đơn hàng không tồn tại.");
//        }
//
//        // Kiểm tra xem sản phẩm có thuộc đơn hàng hay không
//        boolean productInOrder = order.getOnOrderDetails().stream()
//                .anyMatch(detail -> detail.getProduct().getProductID().equals(productId));
//        if (!productInOrder) {
//            throw new IllegalArgumentException("Sản phẩm không thuộc đơn hàng này.");
//        }
//
//        // Kiểm tra xem khách hàng đã đánh giá cho đơn hàng này chưa
//        long commentCount = commentRepository.countByCustomerAndOnlineOrder(customer, order);
//        if (commentCount > 0) {
//            throw new IllegalArgumentException("Bạn đã đánh giá cho đơn hàng này rồi.");
//        }
//
//        // Tạo và lưu bình luận
//        Comment comment = new Comment(commentText, rating, product, customer, order);
//        commentRepository.save(comment);
//    }
    public Long countByCustomerAndOnlineOrder(Customer customer, OnlineOrder onlineOrder){
        return commentRepository.countByCustomerAndOnlineOrder(customer,onlineOrder);
    }
    public Long countByCustomerAndProductAndOnlineOrder(Customer customer,Product product, OnlineOrder onlineOrder){
        return commentRepository.countByCustomerAndOnlineOrder(customer,onlineOrder);
    }


    public List<Comment> getCommentsForProduct(Product product) {
        return commentRepository.findByProduct(product);
    }

    // Phương thức tính điểm đánh giá trung bình của sản phẩm


    public double calculateAverageRating(Product product) {
        List<Comment> comments = getCommentsForProduct(product);
        if (comments.isEmpty()) {
            return 0.0;
        }
        double totalRating = 0;
        for (Comment comment : comments) {
            totalRating += comment.getRate();
        }
        double averageRating = totalRating / comments.size();

        // Sử dụng DecimalFormat để làm tròn đến 2 chữ số thập phân
        DecimalFormat df = new DecimalFormat("##0.00");
        return Double.parseDouble(df.format(averageRating));
    }

}