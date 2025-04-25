package vn.cosbeauty.DTO;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OffDetailDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Long price;
    private int quantity;
    private int discount;

    public OffDetailDTO(Long id, String name, String imageUrl, Long price, int quantity, int discount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }
    public OffDetailDTO()
    {

    }
}
