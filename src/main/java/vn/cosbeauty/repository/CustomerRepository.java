package vn.cosbeauty.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	Customer findByCustomerID(Long customerID);
	Customer findByEmail(String email);

	@Query("SELECT c FROM Customer c WHERE c.phone = :phone")
	Customer findByPhone(@Param("phone") String phone);
	@Query("SELECT c.phone FROM Customer c")
	List<String> listPhone();




}
