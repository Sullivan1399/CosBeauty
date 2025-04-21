package vn.cosbeauty.entity;

import jakarta.persistence.*;
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
    private boolean enabled = false;
    private String verificationToken;

    public Account(String username, String password){
    	this.username = username;
        this.password = password;
    }
    public Account(){
    }


    @Transient
    private String displayName;


}
