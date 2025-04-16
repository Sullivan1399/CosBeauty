package vn.cosbeauty.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ProductDTO {
    private Long id;
    @Column (name = "product_name")
    private String name;
    @Column(name = "image_url")
    private String imageUrl;
    private BigDecimal price;

    public ProductDTO(Long id, String name, String imageUrl,  BigDecimal  price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }
    public ProductDTO()
    {

    }
}
