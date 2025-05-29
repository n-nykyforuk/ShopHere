package online.shop.shophere.Service;

import jakarta.transaction.Transactional;
import online.shop.shophere.Entity.Advertisement;
import online.shop.shophere.Entity.Order;
import online.shop.shophere.Entity.User;
import online.shop.shophere.Repository.AdvertisementRepository;
import online.shop.shophere.Repository.OrderRepository;
import online.shop.shophere.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private OrderRepository orderRepository;
    private AdvertisementRepository advertisementRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, OrderRepository orderRepository, AdvertisementRepository advertisementRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateAvatar(User user, MultipartFile avatarFile) throws IOException {
        if (!avatarFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "-" + avatarFile.getOriginalFilename();
            String uploadDir = "C:/Users/User/OneDrive/Desktop/SpringBoot projects/ShopHere/uploads/";
            File file1 = new File(uploadDir);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            File file = new File(uploadDir + fileName);
            avatarFile.transferTo(file);
            user.setAvatarUrl(fileName);
            userRepository.save(user);
        }
    }

    public void updatePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUser(User updatedUser) {
        logger.info("Updating user: incoming data: username={}, postOffice={}",
                updatedUser.getUsername(), updatedUser.getPostOffice());
        if (updatedUser == null || updatedUser.getId() == null) {
            throw new IllegalArgumentException("User or user ID is null");
        }
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedUser.getId()));
        logger.info("Existing user loaded: username={}, postOffice={}",
                existingUser.getUsername(), existingUser.getPostOffice());

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setPostOffice(updatedUser.getPostOffice());

        logger.info("Before saving: postOffice={}", existingUser.getPostOffice());
        userRepository.save(existingUser);
        logger.info("After saving: postOffice={}", existingUser.getPostOffice());
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) auth.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        throw new RuntimeException("No authenticated user");
    }

    @Transactional
    public void createOrder(String cardNumber, String expiryDate, String cvc, String postOffice, Long adId) {
        User currentUser = getCurrentUser();
        logger.info("Creating order for user: username={}, postOffice={}", currentUser.getUsername(), currentUser.getPostOffice());
        Order order = new Order();
        order.setUser(currentUser);
        order.setCardNumber(cardNumber);
        order.setExpiryDate(expiryDate);
        order.setCvc(cvc);
        order.setPostOffice(postOffice);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);
        logger.info("Order created: orderId={}", order.getId());
        if (adId != null) {
            Advertisement ad = advertisementRepository.findById(adId)
                    .orElseThrow(() -> new RuntimeException("Advertisement not found with id: " + adId));
            ad.setStatus(Advertisement.AdvertisementStatus.SOLD);
            advertisementRepository.save(ad);
            logger.info("Advertisement {} marked as SOLD", adId);
        } else {
            logger.warn("adId is null, cannot mark advertisement as SOLD");
        }
    }

    public void save(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User or email cannot be null");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email " + user.getEmail() + " is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}