package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.service.AccountService;

@Controller
public class LoginController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public String loginPage() {
        return "/web/login";
    }

}
