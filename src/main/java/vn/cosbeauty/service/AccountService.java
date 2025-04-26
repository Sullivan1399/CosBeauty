package vn.cosbeauty.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.cosbeauty.DTO.AccountDTO;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.repository.AccountRepository;
import vn.cosbeauty.repository.CustomerRepository;
import vn.cosbeauty.repository.EmployeeRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService{

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại: " + username);
        }
        if (!account.isEnabled()) {
            throw new RuntimeException("Email chưa được xác thực!");
        }
        if ("ROLE_EMPLOYEE".equals(account.getRole())) {
            Employee employee = employeeRepository.findByEmail(username);
            if (employee == null || !employee.getStatus()) {
                throw new UsernameNotFoundException("Tài khoản nhân viên đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
            }
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole().replace("ROLE_", "")) // Loại bỏ tiền tố ROLE_ vì Spring Security tự động thêm
                .build();
    }
    
    public void registerAccount(Account account, Customer customer) {
    	if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
    	if (customerRepository.findByPhone(customer.getPhone()) != null) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }
    	account.setPassword(passwordEncoder.encode(account.getPassword()));
    	account.setRole("ROLE_CUSTOMER");
    	String token = UUID.randomUUID().toString();
    	account.setVerificationToken(token);
    	account.setEnabled(false);
    	accountRepository.save(account);
    	customerRepository.save(customer);
    	sendVerificationEmail(account.getUsername(), token);
    }
    
    private void sendVerificationEmail(String email, String token) {
//    	SimpleMailMessage message = new SimpleMailMessage();
//    	message.setTo(email);
//    	message.setSubject("Xác thực email cho tài khoản của bạn");
//        message.setText("Vui lòng xác thực email bằng cách nhấp vào liên kết: " +
//                "http://localhost:9090/verify?token=" + token);
//        mailSender.send(message);
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Xác thực email cho tài khoản của bạn");
            String verificationUrl = "http://localhost:9090/verify?token=" + token;
            String content = "<html>" +
                    "<body>" +
                    "<p + style=\"font-size: 16px; \">Vui lòng xác thực email bằng cách nhấp vào nút dưới đây:</p>" +
                    "<a href=\"" + verificationUrl + "\" " +
                    "style=\"display: inline-block; padding: 8px 15px; font-size: 14px; " +
                    "color: #fff; background-color: #28a745; text-decoration: none; border-radius: 5px;\">" +
                    "Xác thực email</a>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public void verifyEmail(String token) {
    	Account account = accountRepository.findByVerificationToken(token);
        if (account != null) {
            account.setEnabled(true);
            account.setVerificationToken(null);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Token không hợp lệ!");
        }
    }
    
    public List<String> findAllEmail()
    {
        List<String> listEmail = accountRepository.findAllEmail();
        if (listEmail.isEmpty())
        {
            return null;
        }
        return listEmail;
    }
//    
//    public Account authenticate(String email, String password) {
//        // Tìm tài khoản theo email
//        Account account = accountRepository.findByUsername(email);
//        if (account != null && account.getPassword().equals(password)) {
//            return account;  // Nếu mật khẩu đúng, trả về tài khoản
//        }
//        return null;  // Nếu không tìm thấy tài khoản hoặc mật khẩu sai, trả về null
//    }
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
     // Thêm hoặc cập nhật tài khoản
    public void save(Account account) {
        accountRepository.save(account);  // Lưu hoặc cập nhật tài khoản
    }
    public List<Account> findByRole(String role) {
        return accountRepository.findByRole(role);
    }
    public List<Account> findAllWithDetails() {
        return accountRepository.findAllWithDetails();
    }
    public List<Account> searchAccounts(String keyword) {
        return accountRepository.searchByIdOrUsername(keyword);
    }


    public List<Account> enrichDisplayNames(List<Account> accounts) {
        for (Account account : accounts) {
            switch (account.getRole()) {
                case "ROLE_CUSTOMER":
                    Customer customer = customerRepository.findByEmail(account.getUsername());
                    account.setDisplayName(customer != null ? customer.getName() : "[Không xác định]");
                    break;
                case "ROLE_EMPLOYEE":
                    Employee employee = employeeRepository.findByEmail(account.getUsername());
                    account.setDisplayName(employee != null ? employee.getName() : "[Không xác định]");
                    break;
                case "ROLE_ADMIN":
                    account.setDisplayName("Admin");
                    break;
                default:
                    account.setDisplayName("[Không xác định]");
            }
        }
        return accounts;
    }
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByUsername(String username) {
        return Optional.ofNullable(accountRepository.findByUsername(username));
    }


    @Transactional
    public void updateAccountAndCustomer(AccountDTO dto) {
        Account acc = accountRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if ("CUSTOMER".equalsIgnoreCase(dto.getRole())) {
            Customer customer = customerRepository.findByEmail(acc.getUsername());
            if (customer == null) {
                throw new RuntimeException("Không tìm thấy khách hàng với email = " + acc.getUsername());
            }

            customer.setName(dto.getNewName());
            customer.setPhone(dto.getNewPhone()); // cập nhật số điện thoại
            customerRepository.save(customer);

        } else if ("EMPLOYEE".equalsIgnoreCase(dto.getRole())) {
            Employee emp = employeeRepository.findByEmail(acc.getUsername());
            if (emp == null) {
                throw new RuntimeException("Không tìm thấy nhân viên với email = " + acc.getUsername());
            }

            emp.setName(dto.getNewName());
            emp.setPhone(dto.getNewPhone()); //  cập nhật số điện thoại
            employeeRepository.save(emp);
        }

        accountRepository.save(acc);
    }

    public Map<String, String> getPhoneMap(List<Account> accounts) {
        Map<String, String> phoneMap = new HashMap<>();

        for (Account account : accounts) {
            String username = account.getUsername(); // thường là email

            switch (account.getRole()) {
                case "ROLE_CUSTOMER":
                    Customer customer = customerRepository.findByEmail(username);
                    phoneMap.put(username, customer != null ? customer.getPhone() : "[Không có SĐT]");
                    break;

                case "ROLE_EMPLOYEE":
                    Employee employee = employeeRepository.findByEmail(username);
                    phoneMap.put(username, employee != null ? employee.getPhone() : "[Không có SĐT]");
                    break;

                default:
                    phoneMap.put(username, "Không có");
            }
        }

        return phoneMap;
    }

    public void registerEmployeeAccount(Account account, Employee employee) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new RuntimeException("Email đã được sử dụng!");
        }
        if (employeeRepository.findByPhone(employee.getPhone()) != null) {
            throw new RuntimeException("Số điện thoại đã được sử dụng!");
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole("ROLE_EMPLOYEE");
        account.setEnabled(true); // Enable account immediately (no email verification)
        accountRepository.save(account);
        employeeRepository.save(employee);
    }

    // Tìm kiếm theo tên (Customer hoặc Employee)
    public List<Account> searchAccountsByName(String keyword) {
        // Tìm Customer theo tên
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(keyword);
        // Tìm Employee theo tên
        List<Employee> employees = employeeRepository.findByNameContainingIgnoreCase(keyword);

        // Lấy danh sách email từ Customer và Employee
        Set<String> emails = new HashSet<>();
        emails.addAll(customers.stream().map(Customer::getEmail).collect(Collectors.toList()));
        emails.addAll(employees.stream().map(Employee::getEmail).collect(Collectors.toList()));

        // Tìm Account có username khớp với email
        return accountRepository.findByUsernameIn(emails);
    }

    // HashMap để lưu token khôi phục tạm thời
    private final Map<String, ResetTokenInfo> resetTokens = new HashMap<>();

    // Inner class để lưu thông tin token
    private static class ResetTokenInfo {
        private final String email;
        private final LocalDateTime expiryDate;

        public ResetTokenInfo(String email, LocalDateTime expiryDate) {
            this.email = email;
            this.expiryDate = expiryDate;
        }

        public String getEmail() {
            return email;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryDate);
        }
    }

    public Account findByEmail(String email) {
        Account account = accountRepository.findByUsername(email);
        System.out.println("findByEmail: Email " + email + " found: " + (account != null));
        return account;
    }

    @Transactional
    public String generateResetPasswordToken(Account account) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(48); // Tăng lên 48 giờ
        resetTokens.put(token, new ResetTokenInfo(account.getUsername(), expiryDate));
        System.out.println("Generated reset token: " + token + " for email: " + account.getUsername());
        System.out.println("Current resetTokens size: " + resetTokens.size());
        System.out.println("Token expiry: " + expiryDate);
        return token;
    }

    public void sendResetPasswordEmail(String email, String token) throws MessagingException {
        String resetLink = "http://localhost:9090/reset-password?token=" + token; // Kiểm tra port
        String subject = "Khôi phục mật khẩu";
        String content = "<html>" +
                "<body>" +
                "<p style=\"font-size: 16px;\">Vui lòng nhấp vào nút dưới đây để đặt lại mật khẩu:</p>" +
                "<a href=\"" + resetLink + "\" " +
                "style=\"display: inline-block; padding: 8px 15px; font-size: 14px; " +
                "color: #fff; background-color: #28a745; text-decoration: none; border-radius: 5px;\">" +
                "Đặt lại mật khẩu</a>" +
                "<p>Liên kết này sẽ hết hạn sau 48 giờ.</p>" +
                "</body>" +
                "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        try {
            mailSender.send(message);
            System.out.println("Reset password email sent to: " + email + " with link: " + resetLink);
        } catch (Exception e) {
            System.err.println("Failed to send reset password email: " + e.getMessage());
            e.printStackTrace();
            throw new MessagingException("Không thể gửi email khôi phục.", e);
        }
    }

    public boolean isValidResetPasswordToken(String token) {
        ResetTokenInfo tokenInfo = resetTokens.get(token);
        if (tokenInfo == null) {
            System.out.println("Token not found: " + token);
            return false;
        }
        if (tokenInfo.isExpired()) {
            System.out.println("Token expired: " + token + ", expiry: " + tokenInfo.expiryDate);
            return false;
        }
        System.out.println("Token valid: " + token + " for email: " + tokenInfo.getEmail());
        return true;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        ResetTokenInfo tokenInfo = resetTokens.get(token);
        if (tokenInfo == null || tokenInfo.isExpired()) {
            System.out.println("Reset attempt failed: Token " + token + " invalid or expired");
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn.");
        }

        Account account = accountRepository.findByUsername(tokenInfo.getEmail());
        if (account == null) {
            System.out.println("Reset attempt failed: Account not found for email: " + tokenInfo.getEmail());
            throw new RuntimeException("Tài khoản không tồn tại.");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        resetTokens.remove(token);
        System.out.println("Password reset for email: " + tokenInfo.getEmail());
    }
    // Thêm phương thức mới để lấy employeeStatusMap
    public Map<String, Boolean> getEmployeeStatusMap(List<Account> accounts) {
        Map<String, Boolean> statusMap = new HashMap<>();
        for (Account account : accounts) {
            if ("ROLE_EMPLOYEE".equals(account.getRole())) {
                // Truy vấn bảng employee sử dụng email (account.username tương ứng với employee.email)
                Employee employee = employeeRepository.findByEmail(account.getUsername());
                if (employee != null) {
                    statusMap.put(account.getUsername(), employee.getStatus());
                } else {
                    statusMap.put(account.getUsername(), false); // Mặc định không hoạt động nếu không tìm thấy
                }
            }
        }
        return statusMap;
    }
    @Transactional
    public void toggleEmployeeStatus(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if ("ROLE_EMPLOYEE".equals(account.getRole())) {
            Employee employee = employeeRepository.findByEmail(account.getUsername());
            if (employee == null) {
                throw new IllegalStateException("Employee not found for email: " + account.getUsername());
            }
            employee.setStatus(!employee.getStatus());
            employeeRepository.save(employee);
        }
    }
}

