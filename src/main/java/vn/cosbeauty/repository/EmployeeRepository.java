package vn.cosbeauty.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.cosbeauty.entity.Employee;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByEmail(String email);
    @Query("SELECT e FROM Employee e WHERE e.email NOT IN (SELECT a.username FROM Account a)")
    List<Employee> findEmployeesWithoutAccount();
    Employee findByPhone(String phone);
    List<Employee> findByNameContainingIgnoreCase(String name);
    List<Employee> findByEmailIn(List<String> emails);

}
