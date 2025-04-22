package vn.cosbeauty.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CartID implements Serializable {
    private Long customerID;

    private Long productID;

    // default constructor

    public CartID(Long customerID, Long productID) {
        this.customerID = customerID;
        this.productID = productID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartID cartID = (CartID) o;
        return Objects.equals(customerID, cartID.customerID) && Objects.equals(productID, cartID.productID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerID, productID);
    }
    // equals() and hashCode()
}

