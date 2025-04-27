package vn.cosbeauty.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import vn.cosbeauty.repository.AccountRepository;
import vn.cosbeauty.service.AccountService;
import vn.cosbeauty.service.CustomerService;
import vn.cosbeauty.service.EmployeeService;
import vn.cosbeauty.service.ImportOrderService;

@Controller
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private CustomerService customerService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ImportOrderService importOrderService;
    @Autowired
    private AccountRepository accountRepository;


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

    @GetMapping("/api/admin/change-password")
    public String showChangePasswordForm() {
        logger.debug("Handling GET /api/admin/change-password");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        logger.debug("User {} has authorities: {}", username, authorities);
        return "admin/change-password-3";
    }

    @GetMapping("admin/accounts/details")
    public String viewAccountDetails(@RequestParam("id") Long id, Model model) {
        System.out.println("Handling /admin/accounts/details?id=" + id);
        try {
            Account account = accountService.findById(id);
            if (account == null) {
                model.addAttribute("error", "Không tìm thấy tài khoản với ID: " + id);
                return "web/account-details";
            }
            Customer customer = accountService.getCustomerByEmail(account.getUsername());
            Employee employee = accountService.getEmployeeByEmail(account.getUsername());
            model.addAttribute("account", account);
            model.addAttribute("customer", customer);
            model.addAttribute("employee", employee);
            return "web/account-details";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin tài khoản: " + e.getMessage());
            return "web/account-details";
        }
    }
    @PostMapping("/admin/accounts/updateEmployee")
    public String updateEmployee(
            @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> System.out.println("Validation error: " + error.toString()));
            model.addAttribute("error", "Vui lòng kiểm tra lại thông tin nhập vào.");
            model.addAttribute("account", employeeService.getAccountByEmployeeId(employee.getEmployeeID()));
            model.addAttribute("employee", employee);
            return "web/account-details";
        }

        try {
            employeeService.saveOrUpdateEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin nhân viên thành công!");
        } catch (Exception e) {
            System.err.println("Error updating employee: " + e.getMessage());
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin nhân viên: " + e.getMessage());
            model.addAttribute("account", employeeService.getAccountByEmployeeId(employee.getEmployeeID()));
            model.addAttribute("employee", employee);
            return "web/account-details";
        }

        return "redirect:/admin/accounts";
    }

    @PostMapping("/api/admin/change-password")
    @ResponseBody
    public ResponseEntity<?> sendChangePasswordEmail() {
        logger.debug("Handling POST /api/admin/change-password");
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.debug("Authenticated username: {}", username);
            var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            logger.debug("User {} has authorities: {}", username, authorities);
            if (username == null || username.equals("anonymousUser")) {
                logger.warn("Unauthenticated access to /api/admin   /change-password");
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            Account account = accountRepository.findByUsername(username);
            if (account == null) {
                logger.warn("Account not found for username: {}", username);
                return ResponseEntity.status(404).body(Map.of("message", "Account not found"));
            }

            String email = account.getUsername();
            logger.info("Initiating password change for email: {}", email);
            String resetToken = accountService.generateResetPasswordToken(account);
            logger.debug("Generated reset token: {}", resetToken);
            accountService.sendResetPasswordEmail(email, resetToken);
            logger.info("Reset password email sent to: {}", email);

            return ResponseEntity.ok(Map.of("message", "A confirmation email has been sent. Please check your email."));
        } catch (Exception e) {
            logger.error("Error sending reset password email: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Failed to send confirmation email: " + e.getMessage()));
        }
    }

    @GetMapping("/api/admin/reset-password")
    public String showResetPasswordFormForAdmin(@RequestParam("token") String token, Model model) {
        logger.debug("Handling GET /api/employee/reset-password with token: {}", token);
        try {
            if (accountService.isValidResetPasswordToken(token)) {
                model.addAttribute("token", token);
                return "admin/reset-password-3";
            } else {
                model.addAttribute("message", "Token không hợp lệ hoặc đã hết hạn.");
                return "web/login";
            }
        } catch (RuntimeException e) {
            logger.error("Error validating reset token: {}", e.getMessage(), e);
            model.addAttribute("message", "Lỗi: " + e.getMessage());
            return "web/login";
        }
    }

    @PostMapping("/api/admin/reset-password")
    public String processResetPasswordForAdmin(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        logger.debug("Handling POST /api/admin/reset-password with token: {}", token);
        try {
            if (!password.equals(confirmPassword)) {
                logger.warn("Passwords do not match for token: {}", token);
                model.addAttribute("message", "Mật khẩu và xác nhận mật khẩu không khớp.");
                model.addAttribute("token", token);
                return "admin/reset-password-3";
            }

            accountService.resetPassword(token, password);
            logger.info("Password reset successfully for token: {}", token);
            model.addAttribute("message", "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập.");
            return "web/login";
        } catch (RuntimeException e) {
            logger.error("Error resetting password: {}", e.getMessage(), e);
            model.addAttribute("message", "Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            model.addAttribute("token", token);
            return "admin/reset-password-3";
        }
    }
}
