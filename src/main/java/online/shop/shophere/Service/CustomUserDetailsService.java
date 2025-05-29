package online.shop.shophere.Service;

import online.shop.shophere.Entity.User;
import online.shop.shophere.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username is null or empty");
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        logger.info("User found: {}", username);
        return user;
    }
}