package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.Comment;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.CommentRepository;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.repository.ProductRepository;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    public void saveReview(Long customerId, Long productId, String commentText, int rating) {
        Customer customer = customerRepository.findByCustomerID(customerId);
        Product product = productRepository.findProductByProductID(productId);

        // Optional: Check if the customer already reviewed this product
        if (commentRepository.existsByCustomerAndProduct(customer, product)) {
            throw new IllegalStateException("Bạn đã đánh giá sản phẩm này rồi.");
        }

        Comment comment = new Comment(commentText, rating, product, customer);
        commentRepository.save(comment);
    }
}