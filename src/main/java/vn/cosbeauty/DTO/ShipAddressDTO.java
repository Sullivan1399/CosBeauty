package vn.cosbeauty.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipAddressDTO {
    private String city;
    private String district;
    private String ward;
    private String houseNo;
    // không-args constructor, getters & setters

    public ShipAddressDTO() {
    }
}
