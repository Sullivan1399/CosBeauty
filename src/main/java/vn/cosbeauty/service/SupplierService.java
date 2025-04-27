package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // Phương thức để lấy danh sách nhà cung cấp với phân trang
    public Page<Supplier> getSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // page - 1 vì Spring bắt đầu từ 0
        return supplierRepository.findAll(pageable);
    }
    public Page<Supplier> getSuppliers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size); // page - 1 vì Spring bắt đầu từ 0
        if (keyword != null && !keyword.isEmpty()) {
            return supplierRepository.findBySupNameContainingIgnoreCase(keyword, pageable);
        } else {
            return supplierRepository.findAll(pageable);  // Nếu không có từ khóa, lấy tất cả nhà cung cấp
        }
    }

    public Supplier getSupplierById(int id) {
        return supplierRepository.findById(id);
    }

    public void addSupplier(Supplier supplier) {
    	supplierRepository.save(supplier);
    }
    
    public void updateSupplier(Supplier supplier) {
    	supplierRepository.save(supplier);
    }
}
