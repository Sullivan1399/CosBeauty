package vn.cosbeauty.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="supplier")
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int supID;
    private String supName;
    private String address;
    private String phone;
    private String email;

    @OneToMany(mappedBy = "supplier")
    private List<Product> products=new ArrayList<>();
    @OneToMany(mappedBy = "supplier")
    private List<ImportOrder> importList = new ArrayList<>();
    
    public Supplier(String supName, String address, String phone, String email){
    	this.supName = supName;
        this.address=address;
        this.phone=phone;
        this.email=email;
    }
    public Supplier(){

    }
}
