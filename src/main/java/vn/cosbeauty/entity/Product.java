package vn.cosbeauty.entity;
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
    private Long productID;
    private String product_name;
    private BigDecimal price;
    private int quantity;
    private String image_url;
    private int discount;
    
    @Lob // Đánh dấu trường này là Large Object
    @Column(columnDefinition = "LONGTEXT")
    private String detail;

    @ManyToOne
    @JoinColumn(name = "catID")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "supID")
    private Supplier supplier;
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Comment> commentsList=new ArrayList<>();
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<On_Order_Detail> onOrderDetails=new ArrayList<>();
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Off_Order_Detail> offOrderDetails=new ArrayList<>();
    @OneToMany(mappedBy = "product")
    private List<Import_Order_Detail> importDetails=new ArrayList<>();
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Cart_Item> cartList=new ArrayList<>();
    
    public Product(String product_name, Category category, Supplier brand,
                    BigDecimal price, int quantity, String image_url, int discount) {
        this.product_name = product_name;
        this.category= category;
        this.supplier = brand;
        this.price = price;
        this.quantity = quantity;
        this.image_url = image_url;
        this.discount = discount;
    }

    public Product()
    {

    }
}
