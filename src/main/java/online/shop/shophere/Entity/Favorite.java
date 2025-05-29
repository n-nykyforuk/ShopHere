package online.shop.shophere.Entity;

import jakarta.persistence.*;

@Entity
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Advertisement advertisement;

    public Favorite(User user, Advertisement advertisement) {
        this.user = user;
        this.advertisement = advertisement;
    }

    public Favorite() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Advertisement getAdvertisement() { return advertisement; }
    public void setAdvertisement(Advertisement advertisement) { this.advertisement = advertisement; }
}
