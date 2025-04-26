package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.cosbeauty.entity.Supplier;
import vn.cosbeauty.repository.SupplierRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSupplier() {
        return supplierRepository.findAll();
    }
    
    public boolean isCategoryExists(String supName) {
    	return supplierRepository.findBySupNameIgnoreCase(supName).isPresent();
    }
    
    public Supplier findByID(int supID) {
    	Supplier supplier = supplierRepository.findById(supID);
    	return supplier;
    }
    
    public void addSupplier(Supplier supplier) {
    	supplierRepository.save(supplier);
    }
    
    public void updateSupplier(Supplier supplier) {
    	supplierRepository.save(supplier);
    }
}
