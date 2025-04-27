package vn.cosbeauty.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.repository.CustomerRepository;
@Service
public class CustomerService {
	@Autowired
    private CustomerRepository customerRepository;
	public Customer findCustomerByID(Long customerID){
        return customerRepository.findByCustomerID(customerID);
    }
	public List<String> listPhone()
    {
        List<String> listPhone = customerRepository.listPhone();
        if (listPhone.isEmpty())
        {
            return null;
        }
        return listPhone;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }	
  
    public Long getCurrentCustomerID() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        Customer customer = customerRepository.findByEmail(username);
        if (customer == null) {
            throw new RuntimeException("Không tìm thấy Customer liên kết với tài khoản này!");
        }
        return customer.getCustomerID();
	}
    
    public boolean isCustomer() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_CUSTOMER"));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

}
