package vn.cosbeauty.service;

import java.util.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    
}

