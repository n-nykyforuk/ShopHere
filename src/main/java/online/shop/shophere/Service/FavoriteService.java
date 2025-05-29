package online.shop.shophere.Service;

import org.springframework.transaction.annotation.Transactional;
import online.shop.shophere.Entity.Advertisement;
import online.shop.shophere.Entity.Favorite;
import online.shop.shophere.Entity.User;
import online.shop.shophere.Repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public void addFavorite(User user, Advertisement advertisement) {
        if (!favoriteRepository.existsByUserAndAdvertisement(user, advertisement)) {
            Favorite favorite = new Favorite(user, advertisement);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(User user, Advertisement advertisement) {
        favoriteRepository.deleteByUserAndAdvertisement(user, advertisement);
    }

    public List<Advertisement> findFavoritesByUser(User user) {
        return favoriteRepository.findByUser(user)
                .stream()
                .map(Favorite::getAdvertisement)
                .collect(Collectors.toList());
    }
}