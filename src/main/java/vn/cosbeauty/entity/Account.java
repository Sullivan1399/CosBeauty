package vn.cosbeauty.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name="account")
@Getter
@Setter
public class Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String username;
    private String password;
    private String role; // CUSTOMER, EMPLOYEE, ADMIN
    public Account(String username, String password){
    	this.username = username;
        this.password = password;
    }
    public Account(){

    }
}
