package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.cosbeauty.entity.Account;
import vn.cosbeauty.service.AccountService;

@Controller
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("account", new Account());
        return "web/register";
	}
	@PostMapping("/register")
    public String registerAccount(@ModelAttribute Account account, Model model) {
        accountService.registerAccount(account);
        model.addAttribute("message", "Vui lòng kiểm tra email để xác thực tài khoản!");
        return "web/register";
    }
	
	@GetMapping("/verify")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		accountService.verifyEmail(token);
		model.addAttribute("message", "Email đã được xác thực thành công! Bạn có thể đăng nhập.");
        return "web/login";
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
}
