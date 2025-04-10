package vn.cosbeauty.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="employee")
@Getter
@Setter
public class Employee{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeID;
    private String email;
    private String name;
    private String address;
    private String phone;
    private Boolean sex;
    private Boolean status;
    private LocalDate birthDate;
    private LocalDate recruitDate;
    @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY)
    private List<Online_Order> onOrderList=new ArrayList<>();
    @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY)
    private List<Offline_Order> offOrderList=new ArrayList<>();
    @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY)
    private List<Import_Order> importList=new ArrayList<>();
    public Employee(){

    }
    public Employee(String name, String email, String address, String phone, Boolean sex, Boolean status, LocalDate birthDate, LocalDate recruitDate){
        this.name=name;
        this.email=email;
        this.address=address;
        this.phone=phone;
        this.sex = sex;
        this.status = status;
        this.birthDate = birthDate;
        this.recruitDate = recruitDate;
    }
}
