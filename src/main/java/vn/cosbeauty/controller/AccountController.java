package vn.cosbeauty.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import vn.cosbeauty.repository.AccountRepository;
import vn.cosbeauty.service.AccountService;
import vn.cosbeauty.service.CustomerService;

@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private CustomerService customerService;
	
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

    @GetMapping("/admin/users")
    public String listUsers(@RequestParam(name = "keyword", required = false) String keyword,
                            @RequestParam(name = "role", required = false) String role,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            Model model) {

        List<Account> accounts;

        if (keyword != null && !keyword.isEmpty()) {
            accounts = accountService.searchAccounts(keyword);
        } else if (role != null && !role.isEmpty()) {
            accounts = accountService.findByRole("ROLE_" + role.toUpperCase());
        } else {
            accounts = accountService.findAllWithDetails();
        }

        accounts = accountService.enrichDisplayNames(accounts);


        model.addAttribute("accounts", accounts);
        model.addAttribute("phoneMap", accountService.getPhoneMap(accounts));
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 1); // nếu chưa có phân trang

        return "web/update-user-account";
    }




    @PostMapping("/admin/users")
    public String updateAccountAndCustomer(@RequestParam("id") Long id,
                                           @RequestParam("newName") String newName,
                                           @RequestParam("role") String role,
                                           @RequestParam("newPhone") String newPhone,
                                           RedirectAttributes redirectAttributes) {
        try {
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setId(id);
            accountDTO.setNewName(newName);
            accountDTO.setRole(role);
            accountDTO.setNewPhone(newPhone);  // nếu bạn cũng cập nhật số điện thoại

            accountService.updateAccountAndCustomer(accountDTO);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/users";
    }





















}
