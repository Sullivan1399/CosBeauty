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
    
    public List<Supplier> findSupplierBySupName(Supplier supplier) {
    	return supplierRepository.findSuppierBySupName(supplier.getSupName());
    }
    
    public Supplier findByID(int supID) {
    	Supplier supplier = supplierRepository.findById(supID)
    		    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    	return supplier;
    }
}
