package online.shop.shophere.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String cardNumber;
    private String expiryDate;
    private String cvc;
    private String postOffice;
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public Order(User user, String cardNumber, String expiryDate, String cvc, String postOffice, LocalDateTime createdAt) {
        this.user = user;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvc = cvc;
        this.postOffice = postOffice;
        this.createdAt = createdAt;
    }

    public Order() {}

    public String getCardNumber() {return cardNumber;}
    public void setCardNumber(String cardNumber) {this.cardNumber = cardNumber;}
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public String getExpiryDate() {return expiryDate;}
    public void setExpiryDate(String expiryDate) {this.expiryDate = expiryDate;}
    public String getCvc() {return cvc;}
    public void setCvc(String cvc) {this.cvc = cvc;}
    public String getPostOffice() {return postOffice;}
    public void setPostOffice(String postOffice) {this.postOffice = postOffice;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}