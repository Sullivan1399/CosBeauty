package vn.cosbeauty.entity;

import java.io.Serializable;

public class CartID implements Serializable {
    private Long customerID;

    private Long productID;

    // default constructor

    public CartID(Long customerID, Long productID) {
        this.customerID = customerID;
        this.productID = productID;
    }

    // equals() and hashCode()
}

