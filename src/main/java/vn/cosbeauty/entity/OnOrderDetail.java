package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "onDetail")
@Getter
@Setter
public class OnOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private BigDecimal cost;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "onOrderID")
    private OnlineOrder onlineOrder;
    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    public OnOrderDetail(Product product, BigDecimal cost, int quantity)
    {
        this.product=product;
        this.cost = cost;
        this.quantity = quantity;
    }

//    public OnOrderDetail(BigDecimal cost, int quantity, OnlineOrder onlineOrder, Product product) {
//        this.cost = cost;
//        this.quantity = quantity;
//        this.onlineOrder = onlineOrder;
//        this.product = product;
//    }

    public OnOrderDetail() {
    }
}
