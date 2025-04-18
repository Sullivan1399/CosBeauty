package vn.cosbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.cosbeauty.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh tại đây nếu cần
}
