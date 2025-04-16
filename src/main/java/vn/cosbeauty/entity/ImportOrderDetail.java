package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "importDetail")
@Getter
@Setter
public class ImportOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private BigDecimal cost;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "importID")
    private ImportOrder importOrder;

    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    public ImportOrderDetail(Product product, BigDecimal cost, int quantity)
    {
        this.product=product;
        this.cost = cost;
        this.quantity = quantity;
    }

    public ImportOrderDetail() {
    }
}
