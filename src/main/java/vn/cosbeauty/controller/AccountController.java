package vn.cosbeauty.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.cosbeauty.DTO.AccountDTO;
import vn.cosbeauty.DTO.RegisterDTO;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.entity.ImportOrder;
import vn.cosbeauty.service.AccountService;
import vn.cosbeauty.service.CustomerService;
import vn.cosbeauty.service.EmployeeService;
import vn.cosbeauty.service.ImportOrderService;

@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private CustomerService customerService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ImportOrderService importOrderService;


    @GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("registerDTO", new RegisterDTO());
        return "web/register";
	}
	@PostMapping("/register")
    public String registerAccount(@ModelAttribute RegisterDTO registerDTO, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
            return "web/register";
        }

        try {
            if (!registerDTO.IsPasswordMatching()) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu và xác nhận mật khẩu không khớp!");
                return "web/register";
            }
            List<String> listPhone= customerService.listPhone();
            if(registerDTO.isPhonePresent(listPhone))
            {
            	bindingResult.rejectValue("phone","error.phone",
                        "Số điện thoại đã tồn tại");
                return "/web/register";
            }
            List<String> listEmail=accountService.findAllEmail();
            if(registerDTO.isEmailPresent(listEmail))
            {
            	bindingResult.rejectValue("username","error.register",
                        "Email đã tồn tại");
                return "/web/register";
            }

            Account account = new Account();
            account.setUsername(registerDTO.getUsername());
            account.setPassword(registerDTO.getPassword());

            Customer customer = new Customer();
            customer.setName(registerDTO.getName());
            customer.setPhone(registerDTO.getPhone());
            String address = String.format("%s, %s, %s, %s",
                    registerDTO.getHouseNo(),
                    registerDTO.getWard() != null ? registerDTO.getWard() : "",
                    registerDTO.getDistrict(),
                    registerDTO.getCity());
            customer.setAddress(address);
            customer.setEmail(registerDTO.getUsername());

            // Đăng ký
            accountService.registerAccount(account, customer);
            model.addAttribute("message", "Vui lòng kiểm tra email để xác thực tài khoản!");
            return "web/register";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "web/register";
        }
    }
	
	@GetMapping("/verify")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		try {
            accountService.verifyEmail(token);
            model.addAttribute("message", "Email đã được xác thực thành công! Bạn có thể đăng nhập.");
            return "web/login";
        } catch (RuntimeException e) {
            model.addAttribute("message", "Xác thực thất bại: " + e.getMessage());
            return "error";
        }
	}
	
	@GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model)
    {
        if (error != null)
        {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        return "/web/login";
    }
    public AccountController(AccountService accountService, ImportOrderService importOrderService) {
        this.accountService = accountService;
        this.importOrderService = importOrderService;
    }

    @GetMapping("/forgot")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("email", "");
        return "web/forgot-password";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            System.out.println("Processing forgot password for email: " + email);
            Account account = accountService.findByEmail(email);
            if (account == null) {
                System.out.println("Email not found: " + email);
                model.addAttribute("error", "Email không tồn tại trong hệ thống.");
                return "web/forgot-password";
            }
            System.out.println("Found account: " + account.getUsername());
            String resetToken = accountService.generateResetPasswordToken(account);
            System.out.println("Generated token: " + resetToken);
            accountService.sendResetPasswordEmail(email, resetToken);
            System.out.println("Email sent to: " + email);
            model.addAttribute("message", "Hướng dẫn khôi phục mật khẩu đã được gửi đến email của bạn.");
            return "web/forgot-password";
        } catch (Exception e) {
            System.err.println("Error sending reset email: " + e.getMessage());
            model.addAttribute("error", "Lỗi khi gửi email khôi phục: " + e.getMessage());
            return "web/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        try {
            if (accountService.isValidResetPasswordToken(token)) {
                model.addAttribute("token", token);
                return "web/reset-password";
            } else {
                model.addAttribute("message", "Token không hợp lệ hoặc đã hết hạn.");
                return "web/login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("message", "Lỗi: " + e.getMessage());
            return "web/login";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        try {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp.");
                model.addAttribute("token", token);
                return "web/reset-password";
            }

            accountService.resetPassword(token, password);
            model.addAttribute("message", "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập.");
            return "web/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            model.addAttribute("token", token);
            return "web/reset-password";
        }
    }

    @GetMapping("/admin/accounts")
    public String manageAdmin(
            @RequestParam(name = "type", defaultValue = "users") String type,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "searchType", defaultValue = "username") String searchType,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(value = "importDate", required = false) String importDate,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        if ("users".equalsIgnoreCase(type)) {
            List<Account> accounts;

            if (keyword != null && !keyword.isEmpty()) {
                if ("name".equalsIgnoreCase(searchType)) {
                    accounts = accountService.searchAccountsByName(keyword);
                } else {
                    accounts = accountService.searchAccounts(keyword);
                }
            } else if (role != null && !role.isEmpty()) {
                accounts = accountService.findByRole("ROLE_" + role.toUpperCase());
            } else {
                accounts = accountService.findAllWithDetails();
            }

            accounts = accountService.enrichDisplayNames(accounts);

            model.addAttribute("employeeStatusMap", accountService.getEmployeeStatusMap(accounts));
            model.addAttribute("accounts", accounts);
            model.addAttribute("phoneMap", accountService.getPhoneMap(accounts));
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchType", searchType);
            model.addAttribute("role", role);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", 1);
            model.addAttribute("activeSection", "account");
        }
        return "web/manage-account";
    }

    @PostMapping("/admin/accounts")
    public String updateAccountAndCustomer(
            @RequestParam("id") Long id,
            @RequestParam("newName") String newName,
            @RequestParam("role") String role,
            @RequestParam("newPhone") String newPhone,
            RedirectAttributes redirectAttributes) {
        try {
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setId(id);
            accountDTO.setNewName(newName);
            accountDTO.setRole(role);
            accountDTO.setNewPhone(newPhone);

            accountService.updateAccountAndCustomer(accountDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/accounts";
    }
    @PostMapping("/admin/accounts/toggle-status")
    public String toggleStatus(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            accountService.toggleEmployeeStatus(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/accounts";
    }
}
