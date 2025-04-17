package vn.cosbeauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.cosbeauty.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

	Account findByVerificationToken(String token);
	
	 @Query("SELECT a.username FROM Account a")
	 List<String> findAllEmail();
}
