package vn.cosbeauty.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class OffOrderResponseDTO {
    private Long offOrderID;
    private BigDecimal cost;
    private String phone;
    private int paymentType;
    private LocalDateTime orderDate;
    private Long customerId;
    private int employeeId;
    private List<OffOrderItemResponseDTO> items;
}
