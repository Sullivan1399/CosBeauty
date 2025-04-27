package vn.cosbeauty.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.cosbeauty.entity.Account;
import org.springframework.data.repository.query.Param;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

	Account findByVerificationToken(String token);
	
	 @Query("SELECT a.username FROM Account a")
	 List<String> findAllEmail();


	// Tìm kiếm tài khoản theo vai trò
	List<Account> findByRole(String role);

	@Query("SELECT a FROM Account a WHERE " +
			"CAST(a.ID AS string) LIKE %:keyword% OR " +
			"LOWER(a.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Account> searchByIdOrUsername(@Param("keyword") String keyword);



	@Query("SELECT a FROM Account a")
	List<Account> findAllWithDetails();

	List<Account> findByUsernameContainingIgnoreCase(String username);
	List<Account> findByUsernameIn(Collection<String> usernames);

}
