package vn.cosbeauty.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="supplier")
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="supid")
    private int supID;
    @Column(name = "sup_name")
    private String supName;
    private String address;
    private String phone;
    private String email;

    @OneToMany(mappedBy = "supplier")
    @JsonBackReference
    private List<Product> products=new ArrayList<>();

    @OneToMany(mappedBy = "supplier")
    @JsonBackReference
    private List<ImportOrder> importList = new ArrayList<>();
    

    public Supplier(){

    }
}
