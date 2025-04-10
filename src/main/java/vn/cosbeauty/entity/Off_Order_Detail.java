package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "offDetail")
@Getter
@Setter
public class Off_Order_Detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private BigDecimal cost;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "offOrderID")
    private Offline_Order offlineOrder;
    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    public Off_Order_Detail(Product product, BigDecimal cost, int quantity)
    {
        this.product=product;
        this.cost = cost;
        this.quantity = quantity;
    }

    public Off_Order_Detail() {
    }
}
