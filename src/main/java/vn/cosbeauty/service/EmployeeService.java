package vn.cosbeauty.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.cosbeauty.entity.Account;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.entity.Employee;
import vn.cosbeauty.repository.AccountRepository;
import vn.cosbeauty.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AccountRepository accountRepository;
    
    public Employee findById(int id) {
    	return employeeRepository.findById(id).orElse(null);
    }

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
  
    public String getCurrentEmployeeName() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        Employee employee = employeeRepository.findByEmail(username);
        if (employee== null) {
            throw new RuntimeException("Không tìm thấy Employee liên kết với tài khoản này!");
        }
        return employee.getName();
    }
    
    public int getCurrentEmployeeID() {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        Employee employee = employeeRepository.findByEmail(username);
        if (employee== null) {
            throw new RuntimeException("Không tìm thấy Employee liên kết với tài khoản này!");
        }
        return employee.getEmployeeID();
    }

    @Transactional
    public Employee saveOrUpdateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        return employeeRepository.save(employee);
    }

    public Account getAccountByEmployeeId(int employeeID) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeID);
        if (employeeOpt.isEmpty()) {
            return null;
        }
        String email = employeeOpt.get().getEmail();
        return accountRepository.findByUsername(email);
    }


    public Page<Employee> findAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

}
