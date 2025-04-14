package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account authenticate(String email, String password) {
        // Tìm tài khoản theo email
        Account account = accountRepository.findByUsername(email);
        if (account != null && account.getPassword().equals(password)) {
            return account;  // Nếu mật khẩu đúng, trả về tài khoản
        }
        return null;  // Nếu không tìm thấy tài khoản hoặc mật khẩu sai, trả về null
    }
}
