package vn.cosbeauty.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="customer")
@Getter
@Setter
public class Customer{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerID;
    private String email;
    private String name;
    private String address;
    private String phone;
    
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Online_Order> onOrderList=new ArrayList<>();
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Offline_Order> offOrderList=new ArrayList<>();
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Comment> commentList=new ArrayList<>();
    @OneToMany(mappedBy = "customer",fetch =FetchType.EAGER)
    private List<Cart_Item> cartList=new ArrayList<>();
    
    public Customer(String name, String email, String address, String phone){
        this.name=name;
        this.email=email;
        this.address=address;
        this.phone=phone;
    }
    public Customer()
    {

    }
}
