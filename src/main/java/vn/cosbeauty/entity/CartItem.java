package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@IdClass(CartID.class)
@Table(name="cartItem")
@Getter
@Setter
public class CartItem {
    @Id
    private Long customerID;
    @Id
    private Long productID;
    private int quantity;

    @ManyToOne
    @JoinColumn (name="customerID", insertable = false, updatable = false)
    private Customer customer;
    @ManyToOne
    @JoinColumn(name="productID", insertable = false, updatable = false)
    private Product product;

    public CartItem(int quantity, Customer customer, Product product)
    {
        this.quantity = quantity;
        this.customer = customer;
        this.product = product;
    }
    public CartItem()
    {

    }

    // Constructor với CartID và số lượng

    public CartItem(Long productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }




}
