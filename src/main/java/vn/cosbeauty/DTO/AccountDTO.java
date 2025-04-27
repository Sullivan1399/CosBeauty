package vn.cosbeauty.DTO;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    private Long id;           // ID dùng để tìm account
    private String newName;    // tên mới
    private String newPhone;
    private String role;       // role để xác định update Customer/Employee

    public AccountDTO() {}


}
