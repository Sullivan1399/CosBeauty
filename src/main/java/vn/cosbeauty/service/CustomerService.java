package vn.cosbeauty.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.cosbeauty.repository.CustomerRepository;

@Service
public class CustomerService {
	@Autowired
    private CustomerRepository customerRepository;
	
	public List<String> listPhone()
    {
        List<String> listPhone = customerRepository.listPhone();
        if (listPhone.isEmpty())
        {
            return null;
        }
        return listPhone;
    }
}
