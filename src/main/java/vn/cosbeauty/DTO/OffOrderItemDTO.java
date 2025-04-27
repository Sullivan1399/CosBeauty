package vn.cosbeauty.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OffOrderItemDTO {
 private Long productId;
 private int quantity;
 private BigDecimal cost;
}

