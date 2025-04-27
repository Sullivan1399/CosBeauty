package vn.cosbeauty.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class OffOrderRequestDTO {
    private Long customerId;
    private int employeeId;
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải đủ 10 số")
    private String phone;
    private BigDecimal cost;
    private int discount;
    private int paymentType;
    private String note;
    private List<OffOrderItemDTO> items;
}
