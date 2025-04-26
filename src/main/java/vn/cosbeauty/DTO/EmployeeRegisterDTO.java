package vn.cosbeauty.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeRegisterDTO {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Số nhà không được để trống")
    private String houseNo;

    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phone;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean sex;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean status;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate birthDate;

    @NotNull(message = "Ngày tuyển dụng không được để trống")
    private LocalDate recruitDate;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    public boolean isEmailPresent(java.util.List<String> emails) {
        return emails.contains(email);
    }

    public boolean isPhonePresent(java.util.List<String> phones) {
        return phones.contains(phone);
    }
}