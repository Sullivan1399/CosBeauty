package vn.cosbeauty.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name="category")
@Getter
@Setter
public class Category{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="catid")
	private int catID;
    @Column(name ="cate_name")
    private String cateName;
    
    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Product> products=new ArrayList<>();
    
    public Category(String cateName){
        this.cateName = cateName;
    }
    public Category(){

    }
}
