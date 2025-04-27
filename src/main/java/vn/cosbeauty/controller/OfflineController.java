package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import vn.cosbeauty.entity.OffOrderDetail;
import vn.cosbeauty.entity.OfflineOrder;
import vn.cosbeauty.repository.OfflineOrderRepository;
import vn.cosbeauty.service.EmployeeService;
import vn.cosbeauty.service.OfflineOrderService;

import java.util.List;

@Controller
public class OfflineController {
	
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private OfflineOrderService offlineOrderService;
	
	@GetMapping("/employee/manage-offline")
	public String manageOfflineOrder(Model model,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
		List<OfflineOrder> offlineOrders;
		if (keyword == null) {
			offlineOrders = offlineOrderService.getAllOffOrder();
		} else {
			offlineOrders = offlineOrderService.searchOrders(keyword);
		}
		model.addAttribute("offOrders", offlineOrders);
		model.addAttribute("keyword", keyword);
		model.addAttribute("hasResults", !offlineOrders.isEmpty());
		
		return "employee/manage-offline";
	}
	
	@GetMapping("/employee/create-offline-order")
    public String formCreateOffOrder(Model model) {
		int id = employeeService.getCurrentEmployeeID();
    	String name = employeeService.getCurrentEmployeeName();
    	model.addAttribute("employeeId", id);
    	model.addAttribute("employeeName", name);
        return "employee/offline-order";
    }
	
	@GetMapping("/employee/offline-detail/{id}")
	public String formOffOrderDetail(@PathVariable Long id, Model model) {
		OfflineOrder offlineOrder = offlineOrderService.getOrderById(id);
		List<OffOrderDetail> offOrderDetails = offlineOrderService.getOffOrderDetail(id);
		model.addAttribute("order", offlineOrder);
		model.addAttribute("offOrderDetails", offOrderDetails);
		return "employee/offline-detail";
	}
}
