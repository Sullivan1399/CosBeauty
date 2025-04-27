package vn.cosbeauty.controller;

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
import vn.cosbeauty.DTO.EmployeeRegisterDTO;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.repository.AccountRepository;
import vn.cosbeauty.repository.EmployeeRepository;
import vn.cosbeauty.service.AccountService;
import vn.cosbeauty.service.EmployeeService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    @GetMapping("/api/employee/profile")
    public String getEmployeeProfile(Model model) {
        logger.debug("Handling GET /api/employee/profile");
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.debug("Authenticated username: {}", username);
            if (username == null || username.equals("anonymousUser")) {
                logger.warn("Unauthenticated access, redirecting to /login");
                return "redirect:/login";
            }

            Account account = accountRepository.findByUsername(username);
            if (account == null) {
                logger.warn("Account not found for username: {}", username);
                return "redirect:/login";
            }

            Employee employee = employeeRepository.findByEmail(account.getUsername());
            if (employee == null) {
                logger.info("Creating new employee for email: {}", account.getUsername());
                employee = new Employee();
                employee.setEmail(account.getUsername());
                employee.setName(account.getDisplayName() != null ? account.getDisplayName() : "New Employee");
                employee.setAddress("");
                employee.setPhone("");
                employee.setSex(true);
                employee.setStatus(true);
                employee.setBirthDate(LocalDate.now());
                employee.setRecruitDate(LocalDate.now());
                employeeRepository.save(employee);
            }

            model.addAttribute("employee", employee);
            logger.debug("Rendering web/employee for employee: {}", employee.getEmail());
            return "web/employee-profile";
        } catch (Exception e) {
            logger.error("Error in getEmployeeProfile: {}", e.getMessage(), e);
            return "redirect:/login";
        }
    }

    @GetMapping("/api/employee/data")
    @ResponseBody
    public ResponseEntity<?> getEmployeeData() {
        logger.debug("Handling GET /api/employee/data");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            logger.warn("Unauthenticated access to /api/employee/data");
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            logger.warn("Account not found for username: {}", username);
            return ResponseEntity.badRequest().body(Map.of("message", "Account not found"));
        }

        Employee employee = employeeRepository.findByEmail(account.getUsername());
        if (employee == null) {
            logger.info("Creating new employee for email: {}", account.getUsername());
            employee = new Employee();
            employee.setEmail(account.getUsername());
            employee.setName(account.getDisplayName() != null ? account.getDisplayName() : "New Employee");
            employee.setAddress("");
            employee.setPhone("");
            employee.setSex(true);
            employee.setStatus(true);
            employee.setBirthDate(LocalDate.now());
            employee.setRecruitDate(LocalDate.now());
            employeeRepository.save(employee);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("name", employee.getName());
        response.put("email", employee.getEmail());
        response.put("address", employee.getAddress());
        response.put("phone", employee.getPhone());
        response.put("sex", employee.getSex());
        response.put("status", employee.getStatus());
        response.put("birthDate", employee.getBirthDate());
        response.put("recruitDate", employee.getRecruitDate());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/employee/profile")
    @ResponseBody
    public ResponseEntity<?> updateEmployeeProfile(@RequestBody EmployeeUpdateRequest request) {
        logger.debug("Handling PUT /api/employee/profile");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            logger.warn("Unauthenticated access to /api/employee/profile");
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            logger.warn("Account not found for username: {}", username);
            return ResponseEntity.badRequest().body(Map.of("message", "Account not found"));
        }

        Employee employee = employeeRepository.findByEmail(account.getUsername());
        if (employee == null) {
            logger.info("Creating new employee for email: {}", account.getUsername());
            employee = new Employee();
            employee.setEmail(account.getUsername());
            employee.setName(account.getDisplayName() != null ? account.getDisplayName() : "New Employee");
            employee.setAddress("");
            employee.setPhone("");
            employee.setSex(true);
            employee.setStatus(true);
            employee.setBirthDate(LocalDate.now());
            employee.setRecruitDate(LocalDate.now());
            employeeRepository.save(employee);
        }

        // Validate input
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            logger.warn("Invalid input: Name is empty");
            return ResponseEntity.badRequest().body(Map.of("message", "Name cannot be empty"));
        }
        if (request.getPhone() != null && !request.getPhone().matches("[0-9]{10,11}")) {
            logger.warn("Invalid input: Phone number {}", request.getPhone());
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid phone number"));
        }
        if (request.getBirthDate() == null) {
            logger.warn("Invalid input: Birth date is empty");
            return ResponseEntity.badRequest().body(Map.of("message", "Birth date cannot be empty"));
        }
        if (request.getRecruitDate() == null) {
            logger.warn("Invalid input: Recruit date is empty");
            return ResponseEntity.badRequest().body(Map.of("message", "Recruit date cannot be empty"));
        }

        employee.setName(request.getName().trim());
        employee.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        employee.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");
        employee.setSex(request.getSex());
        employee.setStatus(request.getStatus());
        employee.setBirthDate(request.getBirthDate());
        employee.setRecruitDate(request.getRecruitDate());

        employeeRepository.save(employee);
        logger.info("Profile updated successfully for employee: {}", employee.getEmail());
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @GetMapping("/api/employee/change-password")
    public String showChangePasswordForm() {
        logger.debug("Handling GET /api/employee/change-password");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        logger.debug("User {} has authorities: {}", username, authorities);
        return "web/change-password-2";
    }

    @PostMapping("/api/employee/change-password")
    @ResponseBody
    public ResponseEntity<?> sendChangePasswordEmail() {
        logger.debug("Handling POST /api/employee/change-password");
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.debug("Authenticated username: {}", username);
            var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            logger.debug("User {} has authorities: {}", username, authorities);
            if (username == null || username.equals("anonymousUser")) {
                logger.warn("Unauthenticated access to /api/employee/change-password");
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

    @GetMapping("/api/employee/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        logger.debug("Handling GET /api/employee/reset-password with token: {}", token);
        try {
            if (accountService.isValidResetPasswordToken(token)) {
                model.addAttribute("token", token);
                return "web/reset-password-2";
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

    @PostMapping("/api/employee/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        logger.debug("Handling POST /api/employee/reset-password with token: {}", token);
        try {
            if (!password.equals(confirmPassword)) {
                logger.warn("Passwords do not match for token: {}", token);
                model.addAttribute("message", "Mật khẩu và xác nhận mật khẩu không khớp.");
                model.addAttribute("token", token);
                return "web/reset-password-2";
            }

            accountService.resetPassword(token, password);
            logger.info("Password reset successfully for token: {}", token);
            model.addAttribute("message", "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập.");
            return "web/login";
        } catch (RuntimeException e) {
            logger.error("Error resetting password: {}", e.getMessage(), e);
            model.addAttribute("message", "Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            model.addAttribute("token", token);
            return "web/reset-password-2";
        }
    }

    public static class EmployeeUpdateRequest {
        private String name;
        private String address;
        private String phone;
        private Boolean sex;
        private Boolean status;
        private LocalDate birthDate;
        private LocalDate recruitDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Boolean getSex() {
            return sex;
        }

        public void setSex(Boolean sex) {
            this.sex = sex;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public LocalDate getRecruitDate() {
            return recruitDate;
        }

        public void setRecruitDate(LocalDate recruitDate) {
            this.recruitDate = recruitDate;
        }
    }
}