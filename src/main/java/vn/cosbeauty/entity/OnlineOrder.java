package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "onlineOrder")
@Getter
@Setter
public class OnlineOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long onOrderID;
    private String name;
    private String address;
    private String phone;
    private BigDecimal cost;
    private int deliveryStatus;
    private int confirm;
    private int paymentType;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime receiveDate;
    private String cancelReason;
    private BigDecimal shippingFee;
    private BigDecimal subtotal;


    @ManyToOne
    @JoinColumn(name = "customerID")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "employeeID")
    private Employee employee;
    @OneToMany(mappedBy = "onlineOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OnOrderDetail> onOrderDetails = new ArrayList<>();
    
    public OnlineOrder(Customer customer, String name, String address, String phone,
                       BigDecimal cost, int deliveryStatus, int confirm, int paymentType,BigDecimal shippingFee, BigDecimal subtotal)
    {
        this.customer = customer;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.cost = cost;
        this.deliveryStatus = deliveryStatus;
        this.confirm = confirm;
        this.paymentType = paymentType;
        this.shippingFee = shippingFee;
        this.subtotal = subtotal;

        this.orderDate = LocalDateTime.now();
    }
    public OnlineOrder()
    {

    }
}
