package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "onDetail")
@Getter
@Setter
public class On_Order_Detail{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private BigDecimal cost;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "onOrderID")
    private Online_Order onlineOrder;
    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    public On_Order_Detail(Product product, BigDecimal cost, int quantity)
    {
        this.product=product;
        this.cost = cost;
        this.quantity = quantity;
    }

    public On_Order_Detail() {
    }
}
