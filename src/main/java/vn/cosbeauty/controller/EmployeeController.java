package vn.cosbeauty.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.cosbeauty.DTO.EmployeeRegisterDTO;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.service.AccountService;
import vn.cosbeauty.service.EmployeeService;

import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/admin/accounts/create-employee")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("employeeRegisterDTO", new EmployeeRegisterDTO());
        return "web/create-employee-account";
    }

    @PostMapping("/admin/accounts/create-employee")
    public String createEmployeeAccount(
            @Valid @ModelAttribute EmployeeRegisterDTO employeeRegisterDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "web/create-employee-account";
        }

        try {
            if (!employeeRegisterDTO.isPasswordMatching()) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword",
                        "Mật khẩu và xác nhận mật khẩu không khớp!");
                return "web/create-employee-account";
            }

            List<String> listPhone = employeeService.listPhone();
            if (employeeRegisterDTO.isPhonePresent(listPhone)) {
                bindingResult.rejectValue("phone", "error.phone",
                        "Số điện thoại đã tồn tại");
                return "web/create-employee-account";
            }

            List<String> listEmail = accountService.findAllEmail();
            if (employeeRegisterDTO.isEmailPresent(listEmail)) {
                bindingResult.rejectValue("email", "error.email",
                        "Email đã tồn tại");
                return "web/create-employee-account";
            }

            Account account = new Account();
            account.setUsername(employeeRegisterDTO.getEmail());
            account.setPassword(employeeRegisterDTO.getPassword());

            Employee employee = new Employee();
            employee.setName(employeeRegisterDTO.getName());
            employee.setEmail(employeeRegisterDTO.getEmail());
            employee.setPhone(employeeRegisterDTO.getPhone());
            String address = String.format("%s, %s, %s, %s",
                    employeeRegisterDTO.getHouseNo(),
                    employeeRegisterDTO.getWard() != null ? employeeRegisterDTO.getWard() : "",
                    employeeRegisterDTO.getDistrict(),
                    employeeRegisterDTO.getCity());
            employee.setAddress(address);
            employee.setSex(employeeRegisterDTO.getSex());
            employee.setStatus(employeeRegisterDTO.getStatus());
            employee.setBirthDate(employeeRegisterDTO.getBirthDate());
            employee.setRecruitDate(employeeRegisterDTO.getRecruitDate());

            accountService.registerEmployeeAccount(account, employee);
            model.addAttribute("message", "Tạo tài khoản nhân viên thành công!");
            return "redirect:/admin/accounts";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "web/create-employee-account";
        }
    }
}