package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.cosbeauty.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
