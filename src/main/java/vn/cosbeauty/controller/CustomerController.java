package vn.cosbeauty.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.repository.AccountRepository;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.service.AccountService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    // Render user.html template for profile page
    @GetMapping("/profile")
    public String getCustomerProfile(Model model) {
        logger.debug("Handling GET /api/customer/profile");
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

            Customer customer = customerRepository.findByEmail(account.getUsername());
            if (customer == null) {
                logger.info("Creating new customer for email: {}", account.getUsername());
                customer = new Customer();
                customer.setEmail(account.getUsername());
                customer.setName(account.getDisplayName() != null ? account.getDisplayName() : "New User");
                customer.setAddress("");
                customer.setPhone("");
                customerRepository.save(customer);
            }

            model.addAttribute("customer", customer);
            logger.debug("Rendering web/user for customer: {}", customer.getEmail());
            return "web/user";
        } catch (Exception e) {
            logger.error("Error in getCustomerProfile: {}", e.getMessage(), e);
            return "redirect:/login";
        }
    }

    // Endpoint to fetch profile data (JSON)
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<?> getCustomerData() {
        logger.debug("Handling GET /api/customer/data");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            logger.warn("Unauthenticated access to /api/customer/data");
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            logger.warn("Account not found for username: {}", username);
            return ResponseEntity.badRequest().body(Map.of("message", "Account not found"));
        }

        Customer customer = customerRepository.findByEmail(account.getUsername());
        if (customer == null) {
            logger.info("Creating new customer for email: {}", account.getUsername());
            customer = new Customer();
            customer.setEmail(account.getUsername());
            customer.setName(account.getDisplayName() != null ? account.getDisplayName() : "New User");
            customer.setAddress("");
            customer.setPhone("");
            customerRepository.save(customer);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("name", customer.getName());
        response.put("email", customer.getEmail());
        response.put("address", customer.getAddress());
        response.put("phone", customer.getPhone());

        return ResponseEntity.ok(response);
    }

    // Update customer profile
    @PutMapping("/profile")
    @ResponseBody
    public ResponseEntity<?> updateCustomerProfile(@RequestBody CustomerUpdateRequest request) {
        logger.debug("Handling PUT /api/customer/profile");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            logger.warn("Unauthenticated access to /api/customer/profile");
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            logger.warn("Account not found for username: {}", username);
            return ResponseEntity.badRequest().body(Map.of("message", "Account not found"));
        }

        Customer customer = customerRepository.findByEmail(account.getUsername());
        if (customer == null) {
            logger.info("Creating new customer for email: {}", account.getUsername());
            customer = new Customer();
            customer.setEmail(account.getUsername());
            customer.setName(account.getDisplayName() != null ? account.getDisplayName() : "New User");
            customer.setAddress("");
            customer.setPhone("");
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

        customer.setName(request.getName().trim());
        customer.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        customer.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");

        customerRepository.save(customer);
        logger.info("Profile updated successfully for customer: {}", customer.getEmail());
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    // Render change-password.html
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        logger.debug("Handling GET /api/customer/change-password");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        logger.debug("User {} has authorities: {}", username, authorities);
        return "web/change-password";
    }

    // Send confirmation email for password change
    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> sendChangePasswordEmail() {
        logger.debug("Handling POST /api/customer/change-password");
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.debug("Authenticated username: {}", username);
            var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            logger.debug("User {} has authorities: {}", username, authorities);
            if (username == null || username.equals("anonymousUser")) {
                logger.warn("Unauthenticated access to /api/customer/change-password");
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            Account account = accountRepository.findByUsername(username);
            if (account == null) {
                logger.warn("Account not found for username: {}", username);
                return ResponseEntity.status(404).body(Map.of("message", "Account not found"));
            }

            String email = account.getUsername(); // Assuming username is the email
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

    // Show reset password form
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        logger.debug("Handling GET /api/customer/reset-password with token: {}", token);
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

    // Process reset password
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        logger.debug("Handling POST /api/customer/reset-password with token: {}", token);
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

    // DTO for updating customer profile
    public static class CustomerUpdateRequest {
        private String name;
        private String address;
        private String phone;

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
    }
}