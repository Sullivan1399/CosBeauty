package vn.cosbeauty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "importOrder")
@Getter
@Setter
public class Import_Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long importID;
    private BigDecimal cost;
    private LocalDateTime importDate;
    private int status;
    private String cancelReason;

    @ManyToOne
    @JoinColumn(name = "employeeID")
    private Employee employee;
    @OneToMany(mappedBy = "importOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Import_Order_Detail> importDetail = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "supID")
    private Supplier supplier;
    
    public Import_Order(Employee employee, BigDecimal cost, int status, Supplier supplier)
    {
        this.employee = employee;
        this.cost = cost;
        this.status = status;
        this.supplier = supplier;
        this.importDate = LocalDateTime.now();
    }
    public Import_Order ()
    {

    }
}
