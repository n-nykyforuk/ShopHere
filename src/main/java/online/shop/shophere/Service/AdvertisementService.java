package online.shop.shophere.Service;

import online.shop.shophere.Entity.Advertisement;
import online.shop.shophere.Entity.User;
import online.shop.shophere.Repository.AdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    public AdvertisementService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public void save(Advertisement advertisement) {
        advertisementRepository.save(advertisement);
    }

    public void delete(Long id) {
        advertisementRepository.deleteById(id);
    }

    public List<Advertisement> findAllByStatus(Advertisement.AdvertisementStatus status) {
        List<Advertisement> advertisements = advertisementRepository.findByStatus(status);
        logger.info("Found {} advertisements with status {}", advertisements.size(), status);
        for (Advertisement ad : advertisements) {
            logger.info("Ad ID: {}, Status: {}, CreatedAt: {}, Class: {}",
                    ad.getId(), ad.getStatus(), ad.getCreatedAt(), ad.getCreatedAt().getClass());
        }
        return advertisements;
    }

    public Advertisement findById(Long id) {
        return advertisementRepository.findById(id).orElse(null);
    }

    public List<Advertisement> findAll() {
        List<Advertisement> advertisements = advertisementRepository.findAll();
        logger.info("Found {} advertisements", advertisements.size());
        for (Advertisement ad : advertisements) {
            logger.info("Ad ID: {}, Status: {}, CreatedAt: {}, Class: {}",
                    ad.getId(), ad.getStatus(), ad.getCreatedAt(), ad.getCreatedAt().getClass());
        }
        return advertisements;
    }

    public List<Advertisement> findByUser(User user) {
        return advertisementRepository.findByUser(user);
    }
}