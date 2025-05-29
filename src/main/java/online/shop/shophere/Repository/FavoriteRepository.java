package online.shop.shophere.Repository;

import online.shop.shophere.Entity.Advertisement;
import online.shop.shophere.Entity.Favorite;
import online.shop.shophere.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    boolean existsByUserAndAdvertisement(User user, Advertisement advertisement);
    void deleteByUserAndAdvertisement(User user, Advertisement advertisement);
}
