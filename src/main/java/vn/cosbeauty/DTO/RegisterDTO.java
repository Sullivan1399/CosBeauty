package vn.cosbeauty.DTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterDTO
{

    @NotBlank(message ="Email không được để trống")
    @Email(message="Email không đúng định dạng")
    @Size(max = 100, message = "Số ký tự tối đa là 100")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(max = 100, message = "Số ký tự tối đa là 100")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String  confirmPassword;

    @NotBlank(message = "Họ và Tên không được để trống")
    @Size(max = 300, message = "Số ký tự tối đa là 300")
    private String name;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    private String ward;

    private String houseNo;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải đủ 10 số")
    private String phone;
    public boolean isEmailPresent(List<String> listEmail)
    {
        if(listEmail != null && !listEmail.isEmpty())
        {
            return listEmail.contains(username);
        }
        return false;
    }
    public boolean IsPasswordMatching()
    {
         return password!=null &&password.equals(confirmPassword);
    }
    public boolean isPhonePresent(List<String> listPhone)
    {
        if(listPhone != null && !listPhone.isEmpty())
        {
            return listPhone.contains(phone);
        }
        return false;
    }
}
