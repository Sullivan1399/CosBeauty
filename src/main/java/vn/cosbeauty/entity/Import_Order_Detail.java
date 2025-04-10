package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "importDetail")
@Getter
@Setter
public class Import_Order_Detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private BigDecimal cost;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "importID")
    private Import_Order importOrder;

    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    public Import_Order_Detail(Product product, BigDecimal cost, int quantity)
    {
        this.product=product;
        this.cost = cost;
        this.quantity = quantity;
    }

    public Import_Order_Detail() {
    }
}
