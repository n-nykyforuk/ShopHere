package online.shop.shophere.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String title;
    private String description;
    private Integer price;
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    @Transient
    private String formattedCreatedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AdvertisementStatus status;

    public Advertisement(String title, String description, Integer price, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt, AdvertisementStatus status) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public enum AdvertisementStatus {
        ACTIVE,
        SOLD,
        DELETED,
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PostLoad
    public void postLoad() {
        if (this.createdAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            this.formattedCreatedAt = this.createdAt.format(formatter);
        } else {
            this.formattedCreatedAt = "No Date Time";
        }
    }

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public String getFormattedCreatedAt() {return formattedCreatedAt;}
    public Advertisement() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public AdvertisementStatus getStatus() {
        return status;
    }
    public void setStatus(AdvertisementStatus status) {
        this.status = status;
    }
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}
