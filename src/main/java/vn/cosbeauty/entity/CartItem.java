package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
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
    @JoinColumn (name="customerID")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name="productID")
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
}
