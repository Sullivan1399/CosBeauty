package vn.cosbeauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.cosbeauty.entity.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	Customer findByCustomerID(Long customerID);
	Customer findByEmail(String email);
	Customer findByPhone(String phone);
	@Query("SELECT c.phone FROM Customer c")
	List<String> listPhone();
}
