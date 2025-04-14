package vn.cosbeauty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.Category;
import vn.cosbeauty.entity.Product;
import vn.cosbeauty.repository.CartRepository;
import vn.cosbeauty.repository.CategoryRepository;
import vn.cosbeauty.repository.ProductRepository;

import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class CosBeautyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosBeautyApplication.class, args);
	}
	@Bean
	CommandLineRunner run(ProductRepository productRepository, CategoryRepository categories) {
		return args -> {
////			String keyword = "Vi"; // giá trị mặc định để test
//
////			List<Product> result = productRepository.findByProductNameContainingIgnoreCase(keyword);
//			List<Product> result = productRepository.findAll();
//			List<Category> result = categories.findAll();
//			List<Product> products = productRepository.findAll();
//			System.out.println("🔍 Kết quả tìm kiếm với keyword: " + products);
//			for (Product p : products) {
//				System.out.println("- " + p.getImageUrl() + " | " + p.getProductName());
//			}
//
//			if (products.isEmpty()) {
//				System.out.println("⚠️ Không tìm thấy sản phẩm nào.");
//			}
		};
	}
}
