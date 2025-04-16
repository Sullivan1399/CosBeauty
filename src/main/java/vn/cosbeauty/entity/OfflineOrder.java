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
    private int paymentType;
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = true)
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "employeeID")
    private Employee employee;
    @OneToMany(mappedBy = "offlineOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OffOrderDetail> offlineDetails=new ArrayList<>();
    
    public OfflineOrder(Customer customer, Employee employee, String phone, BigDecimal cost, int paymentType)
    {
        this.customer = customer;
        this.employee = employee;
        this.phone = phone;
        this.cost = cost;
        this.paymentType = paymentType;
        this.orderDate = LocalDateTime.now();
    }
    public OfflineOrder()
    {

    }
}
