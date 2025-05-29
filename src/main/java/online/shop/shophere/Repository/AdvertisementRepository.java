package online.shop.shophere.Repository;

import online.shop.shophere.Entity.Advertisement;
import online.shop.shophere.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByUser(User user);
    List<Advertisement> findAllByStatus(Advertisement.AdvertisementStatus advertisementStatus);
    List<Advertisement> findByStatus(Advertisement.AdvertisementStatus status);
    List<Advertisement> findAll();
}
