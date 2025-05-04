package vn.cosbeauty.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.cosbeauty.service.OnlineService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private OnlineService onlineService;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        logger.info("Rendering dashboard template: web/dashboard");
        return "web/dashboard";
    }

    @GetMapping("/admin/dashboard/data")
    @ResponseBody
    public Map<String, Object> getDashboardData() {
        try {
            logger.info("Fetching dashboard data");

            // Order Statistics
            Map<String, Long> statusCounts = onlineService.getOrderStatusCounts();
            Map<String, Map<String, Object>> orderStats = Map.of(
                    "pending", Map.of("label", "Chờ xác nhận", "count", statusCounts.get("pending")),
                    "confirmed", Map.of("label", "Đã xác nhận", "count", statusCounts.get("confirmed")),
                    "canceled", Map.of("label", "Đã hủy", "count", statusCounts.get("canceled")),
                    "processing", Map.of("label", "Đang xử lý", "count", statusCounts.get("processing")),
                    "delivering", Map.of("label", "Đang vận chuyển", "count", statusCounts.get("delivering")),
                    "success", Map.of("label", "Thành công", "count", statusCounts.get("success")),
                    "failed", Map.of("label", "Thất bại", "count", statusCounts.get("failed"))
            );

            // Top 10 and Bottom 10 Categories by Sales
            List<Object[]> categorySales;
            try {
                categorySales = entityManager.createNativeQuery(
                                "SELECT c.cate_name, COALESCE(SUM(COALESCE(od.quantity, 0) + COALESCE(offd.quantity, 0)), 0) as sales " +
                                        "FROM category c " +
                                        "LEFT JOIN product p ON p.catid = c.catid " +
                                        "LEFT JOIN on_detail od ON od.productid = p.productid " +
                                        "LEFT JOIN off_detail offd ON offd.productid = p.productid " +
                                        "GROUP BY c.catid, c.cate_name " +
                                        "ORDER BY sales DESC")
                        .getResultList();
                logger.info("Category sales query returned {} rows: {}", categorySales.size(), categorySales);
            } catch (Exception e) {
                logger.error("Error fetching category sales", e);
                categorySales = List.of();
            }

            List<Map<String, Object>> topCategories = categorySales.stream()
                    .limit(10)
                    .map(row -> Map.of("name", row[0] != null ? row[0] : "Unknown", "sales", ((Number) row[1]).longValue()))
                    .collect(Collectors.toList());

            List<Map<String, Object>> bottomCategories = categorySales.stream()
                    .skip(Math.max(0, categorySales.size() - 10))
                    .map(row -> Map.of("name", row[0] != null ? row[0] : "Unknown", "sales", ((Number) row[1]).longValue()))
                    .collect(Collectors.toList());

            // Top 10 and Bottom 10 Products by Sales
            List<Object[]> productSales;
            try {
                productSales = entityManager.createNativeQuery(
                                "SELECT p.product_name, COALESCE(SUM(COALESCE(od.quantity, 0) + COALESCE(offd.quantity, 0)), 0) as sales " +
                                        "FROM product p " +
                                        "LEFT JOIN on_detail od ON od.productid = p.productid " +
                                        "LEFT JOIN off_detail offd ON offd.productid = p.productid " +
                                        "GROUP BY p.productid, p.product_name " +
                                        "ORDER BY sales DESC")
                        .getResultList();
                logger.info("Product sales query returned {} rows", productSales.size());
                if (productSales.size() > 0) {
                    logger.debug("Top product: {} with {} sales", productSales.get(0)[0], productSales.get(0)[1]);
                }
            } catch (Exception e) {
                logger.error("Error fetching product sales", e);
                productSales = List.of();
            }

            List<Map<String, Object>> topProducts = productSales.stream()
                    .limit(10)
                    .map(row -> Map.of("name", row[0] != null ? row[0] : "Unknown", "sales", ((Number) row[1]).longValue()))
                    .collect(Collectors.toList());

            List<Map<String, Object>> bottomProducts = productSales.stream()
                    .skip(Math.max(0, productSales.size() - 10))
                    .map(row -> Map.of("name", row[0] != null ? row[0] : "Unknown", "sales", ((Number) row[1]).longValue()))
                    .collect(Collectors.toList());

            // Revenue by Month
            List<Object[]> revenue;
            try {
                revenue = entityManager.createNativeQuery(
                                "SELECT MONTHNAME(o.order_date), MONTH(o.order_date), COALESCE(SUM(COALESCE(od.cost, 0)), SUM(o.cost)) " +
                                        "FROM online_order o " +
                                        "LEFT JOIN on_detail od ON od.on_orderid = o.on_orderid " +
                                        "WHERE o.order_date >= :startDate " +
                                        "GROUP BY MONTH(o.order_date), MONTHNAME(o.order_date) " +
                                        "UNION ALL " +
                                        "SELECT MONTHNAME(off.order_date), MONTH(off.order_date), COALESCE(SUM(COALESCE(offd.cost, 0)), SUM(off.cost)) " +
                                        "FROM offline_order off " +
                                        "LEFT JOIN off_detail offd ON offd.off_orderid = off.off_orderid " +
                                        "WHERE off.order_date >= :startDate " +
                                        "GROUP BY MONTH(off.order_date), MONTHNAME(off.order_date)")
                        .setParameter("startDate", LocalDateTime.now().minusMonths(4))
                        .getResultList();
                logger.info("Revenue query returned {} rows: {}", revenue.size(), revenue);
            } catch (Exception e) {
                logger.error("Error fetching revenue data", e);
                revenue = List.of();
            }

            List<Map<String, String>> revenueData = revenue.stream()
                    .collect(Collectors.groupingBy(
                            row -> (String) row[0],
                            Collectors.summingDouble(row -> ((Number) row[2]).doubleValue())
                    ))
                    .entrySet().stream()
                    .map(entry -> Map.of(
                            "month", entry.getKey(),
                            "amount", entry.getValue().toString(),
                            "monthNumber", String.valueOf(getMonthNumber(entry.getKey()))
                    ))
                    .sorted((a, b) -> Integer.parseInt(a.get("monthNumber")) - Integer.parseInt(b.get("monthNumber")))
                    .map(entry -> Map.of("month", entry.get("month"), "amount", entry.get("amount")))
                    .collect(Collectors.toList());

            return Map.of(
                    "orderStats", orderStats,
                    "topCategories", topCategories,
                    "bottomCategories", bottomCategories,
                    "topProducts", topProducts,
                    "bottomProducts", bottomProducts,
                    "revenue", revenueData
            );
        } catch (Exception e) {
            logger.error("Error fetching dashboard data", e);
            throw new RuntimeException("Failed to fetch dashboard data", e);
        }
    }

    private int getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january": return 1;
            case "february": return 2;
            case "march": return 3;
            case "april": return 4;
            case "may": return 5;
            case "june": return 6;
            case "july": return 7;
            case "august": return 8;
            case "september": return 9;
            case "october": return 10;
            case "november": return 11;
            case "december": return 12;
            default: return 0;
        }
    }
}