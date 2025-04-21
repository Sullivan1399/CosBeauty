package vn.cosbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.cosbeauty.DTO.ShipAddressDTO;
import vn.cosbeauty.entity.CartItem;
import vn.cosbeauty.entity.Customer;
import vn.cosbeauty.service.CartService;
import vn.cosbeauty.service.CustomerService;

import java.util.List;

@Controller
public class OnlineController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CartService cartService;

    @GetMapping({"/checkout"})
    public String checkout(Model model) {
        Long customerId = customerService.getCurrentCustomerID();
        Customer customer = customerService.findCustomerByID(customerId);
        List<CartItem> cartItems = cartService.getCartItemsByCustomerId(customerId);
        model.addAttribute("shippingAddress", new ShipAddressDTO());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("customer", customer);
        return "web/checkout";

    }
}
