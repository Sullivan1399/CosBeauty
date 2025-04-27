package vn.cosbeauty.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="comment")
@Getter
@Setter
public class Comment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private String comment;
    private int rate;
    @CreationTimestamp
    private LocalDateTime datetime;
    @ManyToOne
    @JoinColumn(name="productID")
    private Product product;
    @ManyToOne
    @JoinColumn(name="customerID")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "onOrderID")
    private OnlineOrder onlineOrder;

    public Comment(String comment, int rate, Product product, Customer customer, OnlineOrder onlineOrder) {
        this.comment = comment;
        this.rate = rate;
        this.product = product;
        this.customer = customer;
        this.onlineOrder = onlineOrder;
    }
    public Comment(String comment, int rate, Product product, Customer customer)
    {
        this.comment = comment;
        this.rate = rate;
        this.product = product;
        this.customer = customer;
    }
    public Comment()
    {

    }

    @Override
    public String toString() {
        return "Comment{" +
                "ID=" + ID +
                ", comment='" + comment + '\'' +
                ", rate=" + rate +
                ", datetime=" + datetime +
                ", product=" + product +
                ", customer=" + customer +
                ", onlineOrder=" + onlineOrder +
                '}';
    }
}
