package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.cosbeauty.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}
