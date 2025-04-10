package vn.cosbeauty.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name="category")
@Getter
@Setter
public class Category{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int catID;
    private String cateName;
    
    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    private List<Product> products=new ArrayList<>();
    
    public Category(String cateName){
        this.cateName = cateName;
    }
    public Category(){

    }
}
