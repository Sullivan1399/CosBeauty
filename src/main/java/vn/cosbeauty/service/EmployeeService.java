package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee findByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null) {
            throw new RuntimeException("Không tìm thấy nhân viên với email: " + email);
        }
        return employee;
    }

    public List<Employee> findEmployeesWithoutAccount() {
        return employeeRepository.findEmployeesWithoutAccount();
    }
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }
    public List<String> listPhone() {
        return employeeRepository.findAll().stream()
                .map(Employee::getPhone)
                .toList();
    }
}
