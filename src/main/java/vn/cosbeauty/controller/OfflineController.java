package vn.cosbeauty.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import vn.cosbeauty.DTO.OffDetailDTO;
import vn.cosbeauty.service.ProductService;

@RestController
@RequestMapping("/api/OffOrder")
@SessionAttributes("OffOrder")
public class OfflineController {

    @Autowired
    private ProductService productService;

    @ModelAttribute("OffOrder")
    public List<OffDetailDTO> OffOrder() {
        return new ArrayList<>();
    }

    @GetMapping("/search")
    public List<OffDetailDTO> search(@RequestParam("q") String keyword) {
        return productService.searchByName(keyword);
    }

    @PostMapping("/addItems")
    public List<OffDetailDTO> addItem(@RequestBody OffDetailDTO item,
                                    @ModelAttribute("OffOrder") List<OffDetailDTO> OffOrder) {
    	OffOrder.add(item);
        return OffOrder;
    }

    @DeleteMapping("/items/{id}")
    public List<OffDetailDTO> removeItem(@PathVariable Long id,
                                       @ModelAttribute("cart") List<OffDetailDTO> OffOrder) {
    	OffOrder.removeIf(p -> p.getId().equals(id));
        return OffOrder;
    }

    @GetMapping("/items")
    public List<OffDetailDTO> getItems(@ModelAttribute("OffOrder") List<OffDetailDTO> OffOrder) {
        return OffOrder;
    }
}
