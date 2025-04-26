package vn.cosbeauty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

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

            // Order Statistics (unchanged, as they work)
            Map<String, Map<String, Object>> orderStats = Map.of(
                    "unconfirmed", Map.of("label", "Đơn hàng chưa xác nhận", "count", getOrderCount("unconfirmed")),
                    "inDelivery", Map.of("label", "Đơn hàng đang giao", "count", getOrderCount("inDelivery")),
                    "delivered", Map.of("label", "Đơn hàng đã giao", "count", getOrderCount("delivered")),
                    "canceled", Map.of("label", "Đơn hàng bị hủy", "count", getOrderCount("canceled"))
            );

            // Top 10 and Bottom 10 Categories by Sales (Native SQL, Fixed Column Names)
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

            // Revenue by Month (Last 4 months, Native SQL, Fallback to online_order/offline_order cost)
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
                            row -> (String) row[0], // Group by month name
                            Collectors.summingDouble(row -> ((Number) row[2]).doubleValue()) // Sum amounts
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
                    "revenue", revenueData
            );
        } catch (Exception e) {
            logger.error("Error fetching dashboard data", e);
            throw new RuntimeException("Failed to fetch dashboard data", e);
        }
    }

    private long getOrderCount(String status) {
        try {
            String query;
            switch (status) {
                case "unconfirmed":
                    query = "SELECT COUNT(*) FROM online_order WHERE confirm = 0";
                    break;
                case "inDelivery":
                    query = "SELECT COUNT(*) FROM online_order WHERE delivery_status = 3";
                    break;
                case "delivered":
                    query = "SELECT COUNT(*) FROM online_order WHERE delivery_status = 1";
                    break;
                case "canceled":
                    query = "SELECT COUNT(*) FROM online_order WHERE delivery_status = 2";
                    break;
                default:
                    return 0;
            }
            return ((Number) entityManager.createNativeQuery(query).getSingleResult()).longValue();
        } catch (Exception e) {
            logger.error("Error counting orders for status: {}", status, e);
            return 0;
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