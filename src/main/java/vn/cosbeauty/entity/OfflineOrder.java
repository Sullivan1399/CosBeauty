package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offlineOrder")
@Getter
@Setter
public class OfflineOrder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offOrderID;
    private BigDecimal cost;
    private String phone;
    private int discount;
    private int paymentType;
    private String note;
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = true)
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "employeeID")
    private Employee employee;
    @OneToMany(mappedBy = "offlineOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OffOrderDetail> offlineDetails=new ArrayList<>();
    
    public OfflineOrder(Customer customer, Employee employee, String phone, int discount, BigDecimal cost, int paymentType, String note)
    {
        this.customer = customer;
        this.employee = employee;
        this.phone = phone;
        this.discount = discount;
        this.cost = cost;
        this.paymentType = paymentType;
        this.note = note;
        this.orderDate = LocalDateTime.now();
    }
    public OfflineOrder()
    {

    }
}
