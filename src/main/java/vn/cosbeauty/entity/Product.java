package vn.cosbeauty.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product")
@Getter
@Setter

public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="productid")
    private Long productID;
    @Column(name = "product_name")
    private String productName;
    private BigDecimal price;
    private int quantity;
    @Column(name = "image_url")
    private String imageUrl;
    private int discount;
    
    @Lob // Đánh dấu trường này là Large Object
    @Column(columnDefinition = "LONGTEXT")
    private String detail;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "catID")
    private Category category;
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "supID")
    private Supplier supplier;
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Comment> commentsList=new ArrayList<>();
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<OnOrderDetail> onOrderDetails=new ArrayList<>();
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<OffOrderDetail> offOrderDetails=new ArrayList<>();
    @OneToMany(mappedBy = "product")
    private List<ImportOrderDetail> importDetails=new ArrayList<>();
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<CartItem> cartList=new ArrayList<>();
    
    public Product(String product_name, Category category, Supplier brand,
                    BigDecimal price, int quantity, String image_url, int discount) {
        this.productName = product_name;
        this.category= category;
        this.supplier = brand;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = image_url;
        this.discount = discount;
    }

    public Product()
    {

    }
}
