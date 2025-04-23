package vn.cosbeauty;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import vn.cosbeauty.DTO.ProductDTO;
import vn.cosbeauty.entity.*;
import vn.cosbeauty.repository.*;
import vn.cosbeauty.service.OnlineService;

import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class CosBeautyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosBeautyApplication.class, args);
	}
	@Bean
	CommandLineRunner run(OnlineService onlineService, CustomerRepository customerRepository) {
		return args -> {
//			Customer customer = customerRepository.findByCustomerID(1L);
//			List<OnlineOrder> allOrders = onlineService.getAllOrdersByCustomerId(customer);
//			System.out.println("Số lượng đơn hàng: " + allOrders.size());
//			for (OnlineOrder order : allOrders) {
//				System.out.println("Mã đơn hàng: " + order.getOnOrderID());
//				System.out.println("Khách hàng ID: " + order.getCustomer().getCustomerID());
//				System.out.println("Tên khách hàng: " + order.getName());
//				System.out.println("Tổng tiền: " + order.getCost());
//				System.out.println("Trạng thái: " + order.getDeliveryStatus());
//				System.out.println("Chi tiết đơn hàng: ");
//				for (OnOrderDetail detail : order.getOnOrderDetails()) {
//					System.out.println("  - Sản phẩm: " + detail.getProduct().getProductName() +
//							", Số lượng: " + detail.getQuantity() +
//							", Giá: " + detail.getProduct().getPrice());
//				}
//				System.out.println("------------------------");
//			}
		};
	}
}
