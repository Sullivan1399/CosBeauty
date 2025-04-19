package vn.cosbeauty.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="customer")
@Getter
@Setter
public class Customer{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerid")
    private Long customerID;
    private String email;
    private String name;
    private String address;
    private String phone;
    
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<OnlineOrder> onOrderList=new ArrayList<>();
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<OfflineOrder> offOrderList=new ArrayList<>();
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private List<Comment> commentList=new ArrayList<>();
    @OneToMany(mappedBy = "customer",fetch =FetchType.LAZY)
    private List<CartItem> cartList=new ArrayList<>();
    
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
