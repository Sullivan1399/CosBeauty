package vn.cosbeauty.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class OffOrderItemResponseDTO {
    private Long id;         
    private Long productId;
    private BigDecimal cost; 
    private int quantity;
}
